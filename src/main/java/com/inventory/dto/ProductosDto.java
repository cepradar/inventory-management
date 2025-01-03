package com.inventory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.model.Productos;

public class ProductosDto {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String category; // Cambiado para recibir categoryId

    // Constructor con @JsonCreator para la deserialización
    @JsonCreator
    public ProductosDto(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") double price,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("categoryId") String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Constructor que recibe un Product
    public ProductosDto(Productos product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCategoryId() {
        return category;
    }

    public void setCategoryId(String categoryId) {
        this.category = categoryId;
    }

    // Método para mostrar cómo deseas que se vea el DTO como String
    @Override
    public String toString() {
        return "ProductDto{id=" + id + ", nombre='" + name + "', precio=" + price + '}';
    }

    public static Productos toProducto(ProductosDto productDto) {
        Productos producto = new Productos();
        producto.setId(productDto.getId());
        producto.setName(productDto.getName());
        producto.setPrice(productDto.getPrice());
        return producto;
    }
}
