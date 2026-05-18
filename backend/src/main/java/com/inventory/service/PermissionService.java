package com.inventory.service;

import com.inventory.dto.PermissionCatalogDto;
import com.inventory.dto.PermissionDto;
import com.inventory.dto.RolePermissionBulkDto;
import com.inventory.model.PermissionAuditLog;
import com.inventory.model.Permisos;
import com.inventory.model.Rol;
import com.inventory.model.RolePermission;
import com.inventory.model.RolePermissionId;
import com.inventory.repository.PermissionAuditLogRepository;
import com.inventory.repository.PermisosRepository;
import com.inventory.repository.RolePermissionRepository;
import com.inventory.repository.RolesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service("permissionService")
public class PermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Autowired
    private PermisosRepository permisosRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private PermissionAuditLogRepository auditLogRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Autorización (uso en @PreAuthorize)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Verifica si el usuario autenticado tiene un permiso específico.
     * Uso: @PreAuthorize("@permissionService.hasPermission(authentication, 'users.create')")
     */
    public boolean hasPermission(Authentication authentication, String permissionCode) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String roleName = resolveRoleName(authentication);
        if (roleName == null) return false;
        return rolePermissionRepository
                .findByRoleNameAndPermissionCode(roleName, permissionCode)
                .map(RolePermission::isActive)
                .orElse(false);
    }

    /**
     * Verifica si el usuario tiene ALGUNO de los permisos listados.
     * Uso: @PreAuthorize("@permissionService.hasAnyPermission(authentication, 'sales.read,sales.create')")
     */
    public boolean hasAnyPermission(Authentication authentication, String codesCommaSeparated) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        String roleName = resolveRoleName(authentication);
        if (roleName == null) return false;
        Set<String> active = new HashSet<>(
                rolePermissionRepository.findActivePermissionCodesByRoleName(roleName));
        for (String code : codesCommaSeparated.split(",")) {
            if (active.contains(code.trim())) return true;
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Consulta de permisos efectivos
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Lista todos los permisos del catálogo indicando si están asignados al rol.
     */
    public List<PermissionDto> getPermissionsForRole(String roleName) {
        List<RolePermission> rolePerms = rolePermissionRepository.findByRoleName(roleName);
        Map<Long, RolePermission> byPermId = new HashMap<>();
        for (RolePermission rp : rolePerms) {
            byPermId.put(rp.getPermission().getId(), rp);
        }

        return permisosRepository.findCatalog().stream().map(p -> {
            PermissionDto dto = new PermissionDto(p);
            RolePermission rp = byPermId.get(p.getId());
            dto.setAssigned(rp != null && rp.isActive());
            if (rp != null) dto.setGrantedBy(rp.getGrantedBy());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Solo códigos de los permisos activos para un rol (para el JWT / frontend).
     */
    public List<String> getActivePermissionCodes(String roleName) {
        return rolePermissionRepository.findActivePermissionCodesByRoleName(roleName);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Catálogo agrupado
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve el catálogo completo de permisos agrupado por módulo y categoría.
     * Cuando se proporciona roleName, cada permiso lleva el estado de asignación.
     */
    public List<PermissionCatalogDto> getCatalog(String roleName) {
        List<Permisos> all = permisosRepository.findCatalog();

        Map<Long, RolePermission> roleMap = new HashMap<>();
        if (roleName != null) {
            rolePermissionRepository.findByRoleName(roleName)
                    .forEach(rp -> roleMap.put(rp.getPermission().getId(), rp));
        }

        // Agrupar por módulo
        Map<String, Map<String, List<PermissionDto>>> tree = new LinkedHashMap<>();
        for (Permisos p : all) {
            PermissionDto dto = new PermissionDto(p);
            if (roleName != null) {
                RolePermission rp = roleMap.get(p.getId());
                dto.setAssigned(rp != null && rp.isActive());
                if (rp != null) dto.setGrantedBy(rp.getGrantedBy());
            }
            String cat = p.getCategoryKey() != null ? p.getCategoryKey() : "__default__";
            tree.computeIfAbsent(p.getModuleKey(), k -> new LinkedHashMap<>())
                .computeIfAbsent(cat, k -> new ArrayList<>())
                .add(dto);
        }

        List<PermissionCatalogDto> result = new ArrayList<>();
        tree.forEach((moduleKey, catMap) -> {
            PermissionCatalogDto mod = new PermissionCatalogDto(moduleKey, toModuleLabel(moduleKey));
            catMap.forEach((catKey, perms) -> {
                PermissionCatalogDto.CategoryGroup group = new PermissionCatalogDto.CategoryGroup(catKey);
                group.setPermissions(perms);
                mod.getCategories().add(group);
            });
            result.add(mod);
        });
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Actualización masiva de permisos de un rol
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void updateRolePermissions(String roleName, List<RolePermissionBulkDto> changes,
                                      String changedBy) {
        Rol rol = rolesRepository.findByName(roleName);
        if (rol == null) throw new IllegalArgumentException("Rol no encontrado: " + roleName);

        for (RolePermissionBulkDto dto : changes) {
            Permisos permiso = permisosRepository.findByCode(dto.getPermissionCode())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Permiso no encontrado: " + dto.getPermissionCode()));

            Optional<RolePermission> existing =
                    rolePermissionRepository.findByRoleNameAndPermissionCode(
                            roleName, dto.getPermissionCode());

            Boolean oldValue;
            if (existing.isPresent()) {
                RolePermission rp = existing.get();
                oldValue = rp.isActive();
                if (rp.isActive() != dto.isActive()) {
                    rp.setActive(dto.isActive());
                    rp.setGrantedBy(changedBy);
                    rp.setGrantedAt(LocalDateTime.now());
                    rolePermissionRepository.save(rp);
                    auditLogRepository.save(new PermissionAuditLog(
                            roleName, dto.getPermissionCode(), changedBy,
                            oldValue, dto.isActive(), dto.getReason()));
                }
            } else {
                oldValue = null;
                RolePermission rp = new RolePermission(rol, permiso, dto.isActive(), changedBy);
                rolePermissionRepository.save(rp);
                auditLogRepository.save(new PermissionAuditLog(
                        roleName, dto.getPermissionCode(), changedBy,
                        oldValue, dto.isActive(), dto.getReason()));
            }
        }

        // Incrementar versión del rol para invalidar cachés
        rol.incrementPermissionsVersion();
        rolesRepository.save(rol);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers privados
    // ─────────────────────────────────────────────────────────────────────────

    private String resolveRoleName(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .map(a -> a.substring(5))
                .orElse(null);
    }

    private String toModuleLabel(String moduleKey) {
        return switch (moduleKey) {
            case "users"     -> "Usuarios";
            case "inventory" -> "Inventario";
            case "clients"   -> "Clientes";
            case "sales"     -> "Ventas";
            case "orders"    -> "Órdenes de Servicio";
            case "audit"     -> "Auditoría";
            case "config"    -> "Configuración";
            case "reports"   -> "Reportes";
            default          -> moduleKey;
        };
    }
}
