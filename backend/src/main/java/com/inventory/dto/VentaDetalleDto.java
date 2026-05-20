package com.inventory.dto;

import java.math.BigDecimal;

/**
 * DTO para representar una línea de detalle de venta en la respuesta API.
 * Compatible con productos físicos y servicios técnicos.
 */
public class VentaDetalleDto {

    // ── Campos producto (retro-compatibles) ──────────────────────────────────
    private String productId;
    private String productNombre;

    // ── Campos servicio ──────────────────────────────────────────────────────
    private Long servicioId;
    private String servicioNombre;

    // ── Discriminador: PRODUCTO | SERVICIO ───────────────────────────────────
    private String tipoItem = "PRODUCTO";

    // ── Campos comunes ───────────────────────────────────────────────────────
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    public VentaDetalleDto() {}

    /** Constructor retro-compatible para ítems de producto. */
    public VentaDetalleDto(String productId, String productNombre,
                           Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.productId = productId;
        this.productNombre = productNombre;
        this.tipoItem = "PRODUCTO";
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    /** Constructor completo con discriminador. */
    public VentaDetalleDto(String productId, String productNombre,
                           Long servicioId, String servicioNombre,
                           String tipoItem,
                           Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        this.productId = productId;
        this.productNombre = productNombre;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.tipoItem = tipoItem != null ? tipoItem : "PRODUCTO";
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductNombre() { return productNombre; }
    public void setProductNombre(String productNombre) { this.productNombre = productNombre; }

    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId = servicioId; }

    public String getServicioNombre() { return servicioNombre; }
    public void setServicioNombre(String servicioNombre) { this.servicioNombre = servicioNombre; }

    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}