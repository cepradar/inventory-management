package com.inventory.controller;

import com.inventory.model.Rol;
import com.inventory.repository.RolePermissionRepository;
import com.inventory.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RolesController {

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @GetMapping
    public ResponseEntity<List<Rol>> listRoles() {
        return ResponseEntity.ok(rolesRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Rol>> listActiveRoles() {
        return ResponseEntity.ok(
                rolesRepository.findAll().stream()
                        .filter(r -> Boolean.TRUE.equals(r.getActive()))
                        .toList());
    }

    @PostMapping
    public ResponseEntity<Rol> createRole(@RequestBody Rol role) {
        if (role == null || role.getName() == null || role.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (rolesRepository.findByName(role.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(rolesRepository.save(role));
    }

    @PutMapping("/{name}")
    public ResponseEntity<Rol> updateRole(@PathVariable String name, @RequestBody Rol updated) {
        Rol existing = rolesRepository.findByName(name);
        if (existing == null) return ResponseEntity.notFound().build();

        if (updated.getColor() != null) existing.setColor(updated.getColor());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        if (updated.getActive() != null) existing.setActive(updated.getActive());
        existing.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(rolesRepository.save(existing));
    }

    @DeleteMapping("/{name}")
    @Transactional
    public ResponseEntity<Void> deleteRole(@PathVariable String name) {
        Rol existing = rolesRepository.findByName(name);
        if (existing == null) return ResponseEntity.notFound().build();

        // Eliminar primero los permisos asignados al rol
        rolePermissionRepository.deleteByRoleName(name);
        rolesRepository.delete(existing);
        return ResponseEntity.noContent().build();
    }
}

