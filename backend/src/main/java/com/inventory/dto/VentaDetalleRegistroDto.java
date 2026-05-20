package com.inventory.dto;

import java.math.BigDecimal;

/**
 * DTO para registrar una línea de venta desde el cliente.
 * Compatible con productos físicos (productId) y servicios (servicioId).
 * El campo tipoItem discrimina: PRODUCTO | SERVICIO
 */
public class VentaDetalleRegistroDto {

    /** ID del producto físico. Requerido si tipoItem = PRODUCTO. */
    private String productId;

    /** ID del servicio técnico. Requerido si tipoItem = SERVICIO. */
    private Long servicioId;

    /** Discriminador: PRODUCTO (default) | SERVICIO */
    private String tipoItem = "PRODUCTO";

    private Integer cantidad;
    private BigDecimal precioUnitario;

    public VentaDetalleRegistroDto() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId = servicioId; }

    public String getTipoItem() { return tipoItem != null ? tipoItem : "PRODUCTO"; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}
