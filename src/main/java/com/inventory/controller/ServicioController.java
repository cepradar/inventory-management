package com.inventory.controller;

import com.inventory.model.Servicio;
import com.inventory.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioRepository repository;

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Servicio>> listar() {
        return ResponseEntity.ok(repository.findByActivoTrue());
    }

    @GetMapping("/listar-todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Servicio>> listarTodos() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crear(@RequestBody Servicio servicio) {
        try {
            Servicio saved = repository.save(servicio);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Servicio servicio) {
        try {
            Servicio existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            existing.setNombre(servicio.getNombre());
            existing.setDescripcion(servicio.getDescripcion());
            existing.setPrecioBase(servicio.getPrecioBase());
            existing.setActivo(servicio.getActivo());
            existing.setRequiereTecnico(servicio.getRequiereTecnico());
            Servicio updated = repository.save(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
