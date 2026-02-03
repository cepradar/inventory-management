package com.inventory.dto;

import java.time.LocalDateTime;

public class MovimientoProductoDto {
    private Long id;
    private Long productId;
    private String productNombre;
    private Integer cantidad;
    private String tipo;
    private String descripcion;
    private String usuarioUsername;
    private String usuarioNombre;
    private LocalDateTime fecha;
    private String referencia;

    // Constructores
    public MovimientoProductoDto() {
    }

    public MovimientoProductoDto(Long id, Long productId, String productNombre, Integer cantidad, 
                                  String tipo, String descripcion, String usuarioUsername, 
                                  String usuarioNombre, LocalDateTime fecha, String referencia) {
        this.id = id;
        this.productId = productId;
        this.productNombre = productNombre;
        this.cantidad = cantidad;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.usuarioUsername = usuarioUsername;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.referencia = referencia;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductNombre() {
        return productNombre;
    }

    public void setProductNombre(String productNombre) {
        this.productNombre = productNombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public String toString() {
        return "MovimientoProductoDto{" +
                "id=" + id +
                ", productId=" + productId +
                ", productNombre='" + productNombre + '\'' +
                ", cantidad=" + cantidad +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", usuarioUsername='" + usuarioUsername + '\'' +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", fecha=" + fecha +
                ", referencia='" + referencia + '\'' +
                '}';
    }
}
