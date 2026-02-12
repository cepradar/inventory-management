package com.inventory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.model.CategoriaElectrodomestico;
import com.inventory.model.CategoryProduct;
import com.inventory.model.Product;

public class ProductDto {

    private String id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private String categoryId; // Cambiado para recibir categoryId
    private Long categoriaElectrodomesticoId;
    private String categoriaElectrodomesticoNombre;
    private Boolean activo;

    // Constructor con @JsonCreator para la deserialización
    @JsonCreator
    public ProductDto(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("price") double price,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("categoryId") String categoryId,
            @JsonProperty("categoriaElectrodomesticoId") Long categoriaElectrodomesticoId,
            @JsonProperty("categoriaElectrodomesticoNombre") String categoriaElectrodomesticoNombre,
            @JsonProperty("activo") Boolean activo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.categoriaElectrodomesticoId = categoriaElectrodomesticoId;
        this.categoriaElectrodomesticoNombre = categoriaElectrodomesticoNombre;
        this.activo = activo;
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
        if (product.getCategoriaElectrodomestico() != null) {
            this.categoriaElectrodomesticoId = product.getCategoriaElectrodomestico().getId();
            this.categoriaElectrodomesticoNombre = product.getCategoriaElectrodomestico().getNombre();
        }
        this.activo = product.isActivo();
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

    public Long getCategoriaElectrodomesticoId() {
        return categoriaElectrodomesticoId;
    }

    public void setCategoriaElectrodomesticoId(Long categoriaElectrodomesticoId) {
        this.categoriaElectrodomesticoId = categoriaElectrodomesticoId;
    }

    public String getCategoriaElectrodomesticoNombre() {
        return categoriaElectrodomesticoNombre;
    }

    public void setCategoriaElectrodomesticoNombre(String categoriaElectrodomesticoNombre) {
        this.categoriaElectrodomesticoNombre = categoriaElectrodomesticoNombre;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
                producto.setActivo(productDto.getActivo() != null ? productDto.getActivo() : true);
                // Crear Category con solo el ID
        CategoryProduct category = new CategoryProduct();
        category.setId(productDto.getCategoryId());
        producto.setCategory(category);
                if (productDto.getCategoriaElectrodomesticoId() != null) {
                        CategoriaElectrodomestico categoriaElectrodomestico = new CategoriaElectrodomestico();
                        categoriaElectrodomestico.setId(productDto.getCategoriaElectrodomesticoId());
                        producto.setCategoriaElectrodomestico(categoriaElectrodomestico);
                }
        return producto;
    }

}