package com.inventory.dto;

import java.math.BigDecimal;

public class VentaDetalleRegistroDto {
    private String productId;
    private Integer cantidad;
    private BigDecimal precioUnitario;

    public VentaDetalleRegistroDto() {}

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}
