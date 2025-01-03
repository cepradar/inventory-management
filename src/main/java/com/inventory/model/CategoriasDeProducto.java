package com.inventory.model;

import jakarta.persistence.*;

@Entity
public class CategoriasDeProducto {
    @Id
    @Column(nullable = false, unique = true)
    private String name;
    private String description;

    public CategoriasDeProducto() {}

    public CategoriasDeProducto(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
