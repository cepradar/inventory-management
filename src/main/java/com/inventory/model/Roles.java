package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Roles {

    @Id
    @Column(nullable = false, unique = true)
    private String name;

    // Constructor vacío requerido por JPA
    public Roles() {
    }

    // Constructor personalizado
    public Roles(String name) {
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
