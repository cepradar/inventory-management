package com.inventory.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.dto.ClienteDto;
import com.inventory.model.Cliente;
import com.inventory.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.crearCliente(cliente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ClienteDto>> buscarCliente(@PathVariable String id) {
        return ResponseEntity.ok(clienteService.buscarCliente(id));
    }

    // Otros endpoints: listar, actualizar, eliminar
}
