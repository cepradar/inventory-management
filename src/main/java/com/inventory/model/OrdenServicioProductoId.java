package com.inventory.model;

import java.io.Serializable;
import java.util.Objects;

public class OrdenServicioProductoId implements Serializable {
    
    private String servicioReparacion;  // Matches the @Id field name
    private Integer regProd;
    
    // Default constructor
    public OrdenServicioProductoId() {
    }
    
    // Constructor with parameters
    public OrdenServicioProductoId(String servicioReparacion, Integer regProd) {
        this.servicioReparacion = servicioReparacion;
        this.regProd = regProd;
    }
    
    // Getters and Setters
    public String getServicioReparacion() {
        return servicioReparacion;
    }
    
    public void setServicioReparacion(String servicioReparacion) {
        this.servicioReparacion = servicioReparacion;
    }
    
    public Integer getRegProd() {
        return regProd;
    }
    
    public void setRegProd(Integer regProd) {
        this.regProd = regProd;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdenServicioProductoId that = (OrdenServicioProductoId) o;
        return Objects.equals(servicioReparacion, that.servicioReparacion) &&
                Objects.equals(regProd, that.regProd);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(servicioReparacion, regProd);
    }
    
    @Override
    public String toString() {
        return "OrdenServicioProductoId{" +
                "servicioReparacion='" + servicioReparacion + '\'' +
                ", regProd=" + regProd +
                '}';
    }
}
