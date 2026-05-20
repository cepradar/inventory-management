package com.inventory.controller;

import com.inventory.dto.ServicioDto;
import com.inventory.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller para el dominio SERVICIO.
 * Rutas bajo /api/servicios — desacoplado de /api/products.
 */
@RestController
@RequestMapping("/api/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    /** Lista todos los servicios (activos e inactivos). Solo ADMIN. */
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMIN','TECNICO')")
    public ResponseEntity<List<ServicioDto>> listar() {
        return ResponseEntity.ok(servicioService.listar());
    }

    /** Lista solo los servicios activos. Disponible para cualquier usuario autenticado. */
    @GetMapping("/activos")
    public ResponseEntity<List<ServicioDto>> listarActivos() {
        return ResponseEntity.ok(servicioService.listarActivos());
    }

    /** Obtiene un servicio por ID. */
    @GetMapping("/{id}")
    public ResponseEntity<ServicioDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }

    /** Crea un nuevo servicio. Solo ADMIN. */
    @PostMapping("/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioDto> crear(@RequestBody ServicioDto dto) {
        return ResponseEntity.ok(servicioService.crear(dto));
    }

    /** Actualiza un servicio. Solo ADMIN. */
    @PutMapping("/actualizar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServicioDto> actualizar(@PathVariable Long id, @RequestBody ServicioDto dto) {
        return ResponseEntity.ok(servicioService.actualizar(id, dto));
    }

    /** Desactiva un servicio (soft-delete). Solo ADMIN. */
    @PatchMapping("/desactivar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> desactivar(@PathVariable Long id) {
        servicioService.desactivar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Servicio desactivado correctamente"));
    }

    /** Elimina definitivamente un servicio. Solo ADMIN. */
    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.ok(Map.of("mensaje", "Servicio eliminado correctamente"));
    }
}
