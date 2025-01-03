package com.inventory.controller;

import com.inventory.model.MovimientosInventario;
import com.inventory.service.MovimientosInventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class MovimientoInventarioController {

    @Autowired
    private MovimientosInventarioService movementService;

  /*@PostMapping("/movement")
    public MovimientosInventario recordMovement(@RequestBody MovimientosInventario movement) {
        return movementService.saveMovement(movement);
    } */
}
