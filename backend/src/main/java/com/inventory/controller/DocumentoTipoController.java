package com.inventory.controller;

import com.inventory.model.DocumentoTipo;
import com.inventory.repository.DocumentoTipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documento-tipos")
@PreAuthorize("hasRole('ADMIN')")
public class DocumentoTipoController {

    @Autowired
    private DocumentoTipoRepository repository;

    @GetMapping
    public ResponseEntity<List<DocumentoTipo>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody DocumentoTipo dto) {
        if (dto.getId() == null || dto.getId().isBlank()) {
            return ResponseEntity.badRequest().body("El campo 'id' es requerido");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            return ResponseEntity.badRequest().body("El campo 'name' es requerido");
        }
        if (repository.existsById(dto.getId())) {
            return ResponseEntity.status(409).body("Ya existe un tipo de documento con el código '" + dto.getId() + "'");
        }
        if (dto.getActivo() == null) dto.setActivo(true);
        return ResponseEntity.ok(repository.save(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody DocumentoTipo dto) {
        return repository.findById(id).map(existing -> {
            if (dto.getName() != null) existing.setName(dto.getName());
            if (dto.getActivo() != null) existing.setActivo(dto.getActivo());
            return ResponseEntity.ok(repository.save(existing));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
