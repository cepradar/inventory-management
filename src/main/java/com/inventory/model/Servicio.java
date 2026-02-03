package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "servicios_catalogo")
public class Servicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre; // Ej: Revisión general, Cambio de motor, Instalación, etc.
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioBase = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "requiere_tecnico")
    private Boolean requiereTecnico = true;
    
    public Servicio() {
    }
    
    public Servicio(String nombre, String descripcion, BigDecimal precioBase) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.activo = true;
        this.requiereTecnico = true;
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
    
    public BigDecimal getPrecioBase() {
        return precioBase;
    }
    
    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public Boolean getRequiereTecnico() {
        return requiereTecnico;
    }
    
    public void setRequiereTecnico(Boolean requiereTecnico) {
        this.requiereTecnico = requiereTecnico;
    }
}
