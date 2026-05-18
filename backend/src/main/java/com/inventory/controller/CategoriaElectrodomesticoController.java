package com.inventory.controller;

import com.inventory.model.CategoriaElectrodomestico;
import com.inventory.repository.CategoriaElectrodomesticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-electrodomestico")
public class CategoriaElectrodomesticoController {

    @Autowired
    private CategoriaElectrodomesticoRepository repository;

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CategoriaElectrodomestico>> listar() {
        return ResponseEntity.ok(repository.findByActivoTrue());
    }

    @GetMapping("/listar-todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategoriaElectrodomestico>> listarTodas() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crear(@RequestBody CategoriaElectrodomestico categoria) {
        try {
            CategoriaElectrodomestico saved = repository.save(categoria);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody CategoriaElectrodomestico dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getNombre() != null) existing.setNombre(dto.getNombre());
            existing.setDescripcion(dto.getDescripcion());
            if (dto.getActivo() != null) existing.setActivo(dto.getActivo());
            return ResponseEntity.ok(repository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
