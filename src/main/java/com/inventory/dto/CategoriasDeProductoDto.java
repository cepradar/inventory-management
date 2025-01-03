package com.inventory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.model.CategoriasDeProducto;
import com.inventory.model.Productos;

public class CategoriasDeProductoDto {

    private String name;
    private String description; // Si usas descripción

    // Constructor con @JsonCreator para la deserialización
    @JsonCreator
    public CategoriasDeProductoDto(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description) {
        this.name = name;
        this.description = description;
    }

    // Constructor que recibe un Category
    public CategoriasDeProductoDto(CategoriasDeProducto category) {
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

    public static CategoriasDeProducto toCategoria(CategoriasDeProductoDto categoriaDto) {
        CategoriasDeProducto categoria = new CategoriasDeProducto();
        categoria.setName(categoriaDto.getName());
        categoria.setDescription(categoriaDto.getDescription());
        return categoria;
    }
}
