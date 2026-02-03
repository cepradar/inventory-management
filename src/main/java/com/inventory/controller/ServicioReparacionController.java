package com.inventory.controller;

import com.inventory.dto.ServicioReparacionDto;
import com.inventory.service.ServicioReparacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios-reparacion")
public class ServicioReparacionController {

    private static final Logger log = LoggerFactory.getLogger(ServicioReparacionController.class);

    @Autowired
    private ServicioReparacionService service;

    @PostMapping("/registrar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registrar(@RequestBody ServicioReparacionDto dto, Authentication auth) {
        try {
            log.info("Recibiendo solicitud de registro de servicio: {}", dto);
            ServicioReparacionDto created = service.registrarServicio(dto, auth.getName());
            log.info("Servicio registrado exitosamente con ID: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Error al registrar servicio: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody ServicioReparacionDto dto) {
        try {
            ServicioReparacionDto updated = service.actualizarServicio(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            ServicioReparacionDto dto = service.obtenerServicioPorId(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> porCliente(@PathVariable String clienteId) {
        try {
            List<ServicioReparacionDto> list = service.obtenerServiciosPorCliente(clienteId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar() {
        try {
            List<ServicioReparacionDto> list = service.obtenerTodosServicios();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable String id, @PathVariable String estado) {
        try {
            ServicioReparacionDto updated = service.cambiarEstado(id, estado);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            service.eliminarServicio(id);
            return ResponseEntity.ok("Eliminado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
