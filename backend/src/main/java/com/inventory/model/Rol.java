package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String color;

    @Column(nullable = true)
    private String description;

    // Constructor vacío requerido por JPA
    public Rol() {
    }

    // Constructor personalizado
    public Rol(String name) {
        this.name = name;
    }

    // Constructor con color
    public Rol(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Rol(String name, String color, String description) {
        this.name = name;
        this.color = color;
        this.description = description;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Roles [name=" + name + ", getName()=" + getName() + ", getClass()=" + getClass() + ", hashCode()="
                + hashCode() + ", toString()=" + super.toString() + "]";
    }

    
}
