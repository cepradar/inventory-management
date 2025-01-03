package com.inventory.dto;

import java.time.LocalDate;

public class MovimientoInventarioDto {

    private String producto;
    private int cantidad;
    private LocalDate fechaMovimiento;

    // Constructor
    public MovimientoInventarioDto(String producto, int cantidad, LocalDate fechaMovimiento) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.fechaMovimiento = fechaMovimiento;
    }

    // Getters y Setters
    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDate getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDate fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
