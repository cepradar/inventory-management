package com.inventory.service;

import com.inventory.model.InventoryMovement;
import com.inventory.repository.InventoryMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    public InventoryMovement saveMovement(InventoryMovement movement) {
        return inventoryMovementRepository.save(movement);
    }
}
