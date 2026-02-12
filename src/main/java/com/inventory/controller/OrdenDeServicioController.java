package com.inventory.controller;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.service.OrdenDeServicioService;
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
public class OrdenDeServicioController {

    private static final Logger log = LoggerFactory.getLogger(OrdenDeServicioController.class);

    @Autowired
    private OrdenDeServicioService service;

    @PostMapping("/registrar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registrar(@RequestBody OrdenDeServicioDto dto, Authentication auth) {
        try {
            log.info("Recibiendo solicitud de registro de servicio: {}", dto);
            OrdenDeServicioDto created = service.registrarServicio(dto, auth.getName());
            log.info("Servicio registrado exitosamente con ID: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Error al registrar servicio: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody OrdenDeServicioDto dto) {
        try {
            OrdenDeServicioDto updated = service.actualizarServicio(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            OrdenDeServicioDto dto = service.obtenerServicioPorId(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> porCliente(@PathVariable String clienteId) {
        try {
            List<OrdenDeServicioDto> list = service.obtenerServiciosPorCliente(clienteId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}/{tipoDocumentoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> porCliente(@PathVariable String clienteId, @PathVariable String tipoDocumentoId) {
        try {
            List<OrdenDeServicioDto> list = service.obtenerServiciosPorCliente(clienteId, tipoDocumentoId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar() {
        try {
            List<OrdenDeServicioDto> list = service.obtenerTodosServicios();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable String id, @PathVariable String estado) {
        try {
            OrdenDeServicioDto updated = service.cambiarEstado(id, estado);
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
