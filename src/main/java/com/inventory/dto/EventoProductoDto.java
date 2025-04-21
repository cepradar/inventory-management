package com.inventory.dto;

import java.time.LocalDateTime;

public class EventoProductoDto {

    private String usuarioUsername;
    private Long tipoDeEventoId;
    private Long productoId;
    private Long clienteNit; // Opcional
    private LocalDateTime fechaEvento;
    private Integer cantidad;
    private String observacion;

    // Getters y Setters

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public Long getTipoDeEventoId() {
        return tipoDeEventoId;
    }

    public void setTipoDeEventoId(Long tipoDeEventoId) {
        this.tipoDeEventoId = tipoDeEventoId;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Long getClienteNit() {
        return clienteNit;
    }

    public void setClienteNit(Long clienteNit) {
        this.clienteNit = clienteNit;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
