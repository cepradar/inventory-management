package com.inventory.controller;

import com.inventory.dto.PermissionCatalogDto;
import com.inventory.dto.PermissionDto;
import com.inventory.dto.RolePermissionBulkDto;
import com.inventory.model.PermissionAuditLog;
import com.inventory.model.Permisos;
import com.inventory.repository.PermissionAuditLogRepository;
import com.inventory.repository.PermisosRepository;
import com.inventory.service.PermissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermisosController {

    @Autowired
    private PermisosRepository permisosRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionAuditLogRepository auditLogRepository;

    // ── Catálogo global ────────────────────────────────────────────────────────

    /** Lista todos los permisos del catálogo */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionDto>> getAllPermisos() {
        List<PermissionDto> result = permisosRepository.findAll().stream()
                .map(PermissionDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Catálogo agrupado por módulo/categoría.
     * Opcional: ?roleName=ADMIN para incluir estado de asignación.
     */
    @GetMapping("/catalog")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionCatalogDto>> getCatalog(
            @RequestParam(required = false) String roleName) {
        return ResponseEntity.ok(permissionService.getCatalog(roleName));
    }

    // ── Permisos por rol ───────────────────────────────────────────────────────

    /** Permisos del catálogo con estado de asignación para el rol indicado */
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionDto>> getPermisosByRole(
            @PathVariable String roleName) {
        return ResponseEntity.ok(permissionService.getPermissionsForRole(roleName));
    }

    /**
     * Actualiza el conjunto de permisos de un rol.
     * Recibe la lista completa de cambios; solo aplica los que difieren del estado actual.
     */
    @PutMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePermisosByRole(
            @PathVariable String roleName,
            @RequestBody List<RolePermissionBulkDto> permisos,
            Authentication authentication) {
        try {
            String changedBy = authentication != null ? authentication.getName() : "system";
            permissionService.updateRolePermissions(roleName, permisos, changedBy);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Permisos del usuario autenticado ──────────────────────────────────────

    /**
     * Devuelve los códigos de permisos activos del usuario que llama.
     * Uso: frontend los almacena para decidir qué mostrar.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getMyPermissions(Authentication authentication) {
        String roleName = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst()
                .map(a -> a.substring(5))
                .orElse(null);
        if (roleName == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(permissionService.getActivePermissionCodes(roleName));
    }

    // ── CRUD de definiciones de permisos ──────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionDto> createPermission(
            @RequestBody Permisos permiso) {
        if (permisosRepository.findByCode(permiso.getCode()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Permisos saved = permisosRepository.save(permiso);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PermissionDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PermissionDto> updatePermission(
            @PathVariable Long id, @RequestBody Permisos updated) {
        return permisosRepository.findById(id).map(p -> {
            p.setLabel(updated.getLabel());
            p.setModuleKey(updated.getModuleKey());
            p.setCategoryKey(updated.getCategoryKey());
            p.setActionKey(updated.getActionKey());
            p.setUiVisible(updated.getUiVisible());
            p.setCritical(updated.getCritical());
            p.setActive(updated.getActive());
            return ResponseEntity.ok(new PermissionDto(permisosRepository.save(p)));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Audit log ─────────────────────────────────────────────────────────────

    @GetMapping("/audit/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PermissionAuditLog>> getAuditByRole(
            @PathVariable String roleName) {
        return ResponseEntity.ok(
                auditLogRepository.findByRoleNameOrderByChangedAtDesc(roleName));
    }
}
