package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "marca_electrodomestico")
public class MarcaElectrodomestico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String nombre;
    
    private Boolean activo = true;
    
    public MarcaElectrodomestico() {
    }
    
    public MarcaElectrodomestico(String nombre) {
        this.nombre = nombre;
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
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
