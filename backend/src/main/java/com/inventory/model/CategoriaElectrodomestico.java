package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias_electrodomestico")
public class CategoriaElectrodomestico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String nombre; // Nevera, Lavadora, Microondas, Televisor, etc.
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    public CategoriaElectrodomestico() {
    }
    
    public CategoriaElectrodomestico(String nombre) {
        this.nombre = nombre;
        this.activo = true;
    }
    
    public CategoriaElectrodomestico(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
