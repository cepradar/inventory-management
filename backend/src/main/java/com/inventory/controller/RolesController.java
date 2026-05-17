package com.inventory.controller;

import com.inventory.model.Rol;
import com.inventory.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RolesController {

    @Autowired
    private RolesRepository rolesRepository;

    @GetMapping
    public ResponseEntity<List<Rol>> listRoles() {
        return ResponseEntity.ok(rolesRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Rol> createRole(@RequestBody Rol role) {
        if (role == null || role.getName() == null || role.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (rolesRepository.findByName(role.getName()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Rol created = rolesRepository.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
