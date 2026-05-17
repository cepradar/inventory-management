package com.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventory.dto.ClienteDto;
import com.inventory.service.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;

    @PostMapping("/crear")
    public ResponseEntity<ClienteDto> crearCliente(@RequestBody ClienteDto clienteDto) {
        try {
            return ResponseEntity.ok(clienteService.crearCliente(clienteDto));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ClienteDto>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{documento}")
    public ResponseEntity<List<ClienteDto>> buscarClientesPorDocumento(@PathVariable String documento) {
        return ResponseEntity.ok(clienteService.buscarClientesPorDocumento(documento));
    }

    @GetMapping("/{documento}/{tipoDocumentoId}")
    public ResponseEntity<ClienteDto> buscarCliente(@PathVariable String documento, @PathVariable String tipoDocumentoId) {
        Optional<ClienteDto> cliente = clienteService.buscarCliente(documento, tipoDocumentoId);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{documento}/{tipoDocumentoId}")
    public ResponseEntity<ClienteDto> actualizarCliente(@PathVariable String documento, @PathVariable String tipoDocumentoId, @RequestBody ClienteDto clienteDto) {
        return ResponseEntity.ok(clienteService.actualizarCliente(documento, tipoDocumentoId, clienteDto));
    }

    @DeleteMapping("/eliminar/{documento}/{tipoDocumentoId}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String documento, @PathVariable String tipoDocumentoId) {
        clienteService.eliminarCliente(documento, tipoDocumentoId);
        return ResponseEntity.ok().build();
    }
}
