package com.inventory.model;

import jakarta.persistence.*;

@Entity
public class Permisos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ejemplo: "CREATE_PRODUCT", "DELETE_PRODUCT", etc.

    // Constructor vacío requerido por JPA
    public Permisos() {}

    public Permisos(String name) {
        this.name = name;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
