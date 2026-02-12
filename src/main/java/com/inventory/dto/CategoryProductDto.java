package com.inventory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.model.CategoryProduct;

public class CategoryProductDto {

    private String id;
    private String name;
    private String description; // Si usas descripción

    // Constructor con @JsonCreator para la deserialización
    @JsonCreator
    public CategoryProductDto(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Constructor que recibe un Category
    public CategoryProductDto(CategoryProduct category) {        
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
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

    @Override
    public String toString() {
        return "CategoriaDto [name=" + name + ", description=" + description + "]";
    }

    public static CategoryProduct toCategoria(CategoryProductDto category) {
        CategoryProduct categoria = new CategoryProduct();
        categoria.setId(category.getId());
        categoria.setName(category.getName());
        categoria.setDescription(category.getDescription());
        return categoria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
