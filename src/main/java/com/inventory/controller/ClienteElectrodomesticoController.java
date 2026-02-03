package com.inventory.controller;

import com.inventory.dto.ClienteElectrodomesticoDto;
import com.inventory.dto.ErrorResponse;
import com.inventory.service.ClienteElectrodomesticoService;
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
@RequestMapping("/api/cliente-electrodomestico")
public class ClienteElectrodomesticoController {

    private static final Logger log = LoggerFactory.getLogger(ClienteElectrodomesticoController.class);

    @Autowired
    private ClienteElectrodomesticoService service;

    @PostMapping("/registrar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registrar(@RequestBody ClienteElectrodomesticoDto dto, Authentication auth) {
        try {
            log.info("Recibiendo solicitud de registro de electrodoméstico: {}", dto);
            ClienteElectrodomesticoDto creado = service.registrar(dto, auth.getName());
            log.info("Electrodoméstico registrado exitosamente con ID: {}", creado.getId());
            return ResponseEntity.ok(creado);
        } catch (Exception e) {
            log.error("Error al registrar electrodoméstico: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ClienteElectrodomesticoDto dto) {
        try {
            ClienteElectrodomesticoDto actualizado = service.actualizar(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            ClienteElectrodomesticoDto dto = service.obtenerPorId(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarPorCliente(@PathVariable String clienteId) {
        try {
            List<ClienteElectrodomesticoDto> list = service.listarPorCliente(clienteId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarTodos() {
        try {
            List<ClienteElectrodomesticoDto> list = service.listarTodos();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.ok("Eliminado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
