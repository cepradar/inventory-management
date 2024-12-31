package com.inventory.controller;

import com.inventory.model.InventoryMovement;
import com.inventory.service.InventoryMovementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryMovementService movementService;

    @PostMapping("/movement")
    public InventoryMovement recordMovement(@RequestBody InventoryMovement movement) {
        return movementService.saveMovement(movement);
    }
}
