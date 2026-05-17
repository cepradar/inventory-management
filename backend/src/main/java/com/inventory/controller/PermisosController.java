package com.inventory.controller;

import com.inventory.dto.PermisoAsignacionDto;
import com.inventory.model.Permisos;
import com.inventory.model.Permisos_Usuario;
import com.inventory.model.Rol;
import com.inventory.repository.PermisosRepository;
import com.inventory.repository.PermisosUsuarioRepository;
import com.inventory.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class PermisosController {

    @Autowired
    private PermisosRepository permisosRepository;

    @Autowired
    private PermisosUsuarioRepository permisosUsuarioRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Permisos>> getAllPermisos() {
        return ResponseEntity.ok(permisosRepository.findAll());
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PermisoAsignacionDto>> getPermisosByRole(@PathVariable String roleName) {
        List<PermisoAsignacionDto> result = permisosRepository.findAll().stream()
            .map(permiso -> {
                boolean active = permisosUsuarioRepository
                    .findByRoleNameAndPermissionName(roleName, permiso.getName())
                    .map(Permisos_Usuario::isActive)
                    .orElse(false);
                return new PermisoAsignacionDto(permiso.getId(), permiso.getName(), active);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePermisosByRole(@PathVariable String roleName,
                                                  @RequestBody List<PermisoAsignacionDto> permisos) {
        Rol role = rolesRepository.findByName(roleName);
        if (role == null) {
            return ResponseEntity.badRequest().body("Rol no encontrado");
        }

        for (PermisoAsignacionDto permisoDto : permisos) {
            Permisos permiso = permisosRepository.findById(permisoDto.getPermissionId()).orElse(null);
            if (permiso == null) {
                continue;
            }

            Permisos_Usuario asignacion = permisosUsuarioRepository
                .findByRoleNameAndPermissionName(roleName, permiso.getName())
                .orElseGet(() -> new Permisos_Usuario(role, permiso, false));

            asignacion.setActive(permisoDto.isActive());
            asignacion.setRole(role);
            asignacion.setPermission(permiso);
            permisosUsuarioRepository.save(asignacion);
        }

        return ResponseEntity.ok().build();
    }
}
