package com.inventory.controller;

import com.inventory.dto.MarcaElectrodomesticoDto;
import com.inventory.service.MarcaElectrodomesticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas-electrodomestico")
@PreAuthorize("hasRole('ADMIN')")
public class MarcaElectrodomesticoController {

    @Autowired
    private MarcaElectrodomesticoService service;

    @GetMapping("/listar")
    public ResponseEntity<List<MarcaElectrodomesticoDto>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody MarcaElectrodomesticoDto dto) {
        try {
            MarcaElectrodomesticoDto created = service.crear(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody MarcaElectrodomesticoDto dto) {
        try {
            MarcaElectrodomesticoDto updated = service.actualizar(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Marca eliminada");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
