package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private int quantity;

    @Column(nullable = false)
    private boolean activo = true;

    @JsonBackReference // Evita la serialización recursiva
    @ManyToOne(fetch = FetchType.EAGER)  // Puedes cambiarlo a LAZY si es necesario
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryProduct category;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_electrodomestico_id", nullable = true)
    private CategoriaElectrodomestico categoriaElectrodomestico;
    public Product() {}

    public Product(String name, double price, int quantity, CategoryProduct category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public CategoryProduct getCategory() {
        return category;
    }

    public void setCategory(CategoryProduct category) {
        this.category = category;
    }

    public CategoriaElectrodomestico getCategoriaElectrodomestico() {
        return categoriaElectrodomestico;
    }

    public void setCategoriaElectrodomestico(CategoriaElectrodomestico categoriaElectrodomestico) {
        this.categoriaElectrodomestico = categoriaElectrodomestico;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

}
