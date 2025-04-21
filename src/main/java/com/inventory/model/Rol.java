package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @Column(nullable = false, unique = true)
    private String name;

    // Constructor vacío requerido por JPA
    public Rol() {
    }

    // Constructor personalizado
    public Rol(String name) {
        this.name = name;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Roles [name=" + name + ", getName()=" + getName() + ", getClass()=" + getClass() + ", hashCode()="
                + hashCode() + ", toString()=" + super.toString() + "]";
    }

    
}
