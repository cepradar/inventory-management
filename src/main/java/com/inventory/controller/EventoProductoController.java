package com.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.inventory.model.EventoProducto;
import com.inventory.service.EventoProductoService;

@RestController
@RequestMapping("/api/eventos")
public class EventoProductoController {

    @Autowired
    private EventoProductoService service;

    @PostMapping
    public ResponseEntity<EventoProducto> registrarEvento(@RequestBody EventoProducto evento) {
        EventoProducto creado = service.registrarEvento(evento);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @GetMapping("/producto/{id}")
    public List<EventoProducto> eventosPorProducto(@PathVariable Long id) {
        return service.obtenerEventosPorProducto(id);
    }

    // ...añadir búsquedas por cliente, fecha, etc, en caso de ser necesario.
}
