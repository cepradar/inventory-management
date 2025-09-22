package com.inventory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.model.CategoryProduct;
import com.inventory.model.Product;

public class ProductDto {

    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String categoryId; // Cambiado para recibir categoryId

    // Constructor con @JsonCreator para la deserialización
    @JsonCreator
    public ProductDto(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") double price,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("categoryId") String categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    // Constructor que recibe un Product
    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        if (product.getCategory() != null) {
            this.categoryId = product.getCategory().getId();
        }
    }
    

    // Getters y setters
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
        return categoryId;
    }

    public void setCategoryId(String category) {
        this.categoryId = category;
    }

    // Método para mostrar cómo deseas que se vea el DTO como String
    @Override
    public String toString() {
        return "ProductDto{id=" + id + ", nombre='" + name + "', precio=" + price + '}';
    }

    public static Product toProducto(ProductDto productDto) {
        Product producto = new Product();
        producto.setId(productDto.getId());
        producto.setName(productDto.getName());
        producto.setPrice(productDto.getPrice());
        producto.setDescription(productDto.getDescription());
        producto.setQuantity(productDto.getQuantity());
          // Crear Category con solo el ID
        CategoryProduct category = new CategoryProduct();
        category.setId(productDto.getCategoryId());
        producto.setCategory(category);
        return producto;
    }

}