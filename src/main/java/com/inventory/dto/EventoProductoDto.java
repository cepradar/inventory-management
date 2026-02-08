package com.inventory.dto;

import java.time.LocalDateTime;

public class EventoProductoDto {

    private String usuarioUsername;
    private String tipoDeEventoId;
    private String productoId;
    private String clienteId; // Opcional
    private String clienteTipoDocumentoId; // Opcional
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

    public String getTipoDeEventoId() {
        return tipoDeEventoId;
    }

    public void setTipoDeEventoId(String tipoDeEventoId) {
        this.tipoDeEventoId = tipoDeEventoId;
    }

    public String getProductoId() {
        return productoId;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteTipoDocumentoId() {
        return clienteTipoDocumentoId;
    }

    public void setClienteTipoDocumentoId(String clienteTipoDocumentoId) {
        this.clienteTipoDocumentoId = clienteTipoDocumentoId;
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
