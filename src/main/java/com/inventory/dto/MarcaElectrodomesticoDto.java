package com.inventory.dto;

import com.inventory.model.MarcaElectrodomestico;

public class MarcaElectrodomesticoDto {
    private Long id;
    private String nombre;
    private Boolean activo;
    
    public MarcaElectrodomesticoDto() {
    }
    
    public MarcaElectrodomesticoDto(MarcaElectrodomestico marca) {
        this.id = marca.getId();
        this.nombre = marca.getNombre();
        this.activo = marca.getActivo();
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
