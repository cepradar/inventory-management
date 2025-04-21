package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MovimientoInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "entrada" o "salida"
    private int quantity;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public MovimientoInventario() {}

    public MovimientoInventario(String type, int quantity, Product product) {
        this.type = type;
        this.quantity = quantity;
        this.date = LocalDateTime.now();
        this.product = product;
    }

    // Getters y Setters
}
