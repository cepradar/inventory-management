package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MovimientosInventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "entrada" o "salida"
    private int quantity;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Productos product;

    public MovimientosInventario() {}

    public MovimientosInventario(String type, int quantity, Productos product) {
        this.type = type;
        this.quantity = quantity;
        this.date = LocalDateTime.now();
        this.product = product;
    }

    // Getters y Setters
}
