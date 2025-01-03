package com.inventory.service;

import com.inventory.repository.MovimientosInventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovimientosInventarioService {

    @Autowired
    private MovimientosInventarioRepository inventoryMovementRepository;

    public MovimientosInventarioService saveMovement(MovimientosInventarioService movement) {
        return inventoryMovementRepository.save(movement);
    }
}
