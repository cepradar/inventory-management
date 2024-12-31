package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "entrada" o "salida"
    private int quantity;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public InventoryMovement() {}

    public InventoryMovement(String type, int quantity, Product product) {
        this.type = type;
        this.quantity = quantity;
        this.date = LocalDateTime.now();
        this.product = product;
    }

    // Getters y Setters
}
