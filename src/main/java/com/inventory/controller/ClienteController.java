package com.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{nit}")
    public ResponseEntity<Cliente> buscarCliente(@PathVariable Long nit) {
        return ResponseEntity.ok(clienteService.buscarCliente(nit));
    }

    // Otros endpoints: listar, actualizar, eliminar
}
