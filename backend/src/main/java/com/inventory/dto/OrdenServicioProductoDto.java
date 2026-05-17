package com.inventory.dto;

import java.math.BigDecimal;

public class OrdenServicioProductoDto {
    private String servicioReparacionId;
    private String productId;
    private String productName;
    private Integer cantidad;
    private Integer regProd;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String claveCompuesta;

    public OrdenServicioProductoDto() {}

    public OrdenServicioProductoDto(String servicioReparacionId, String productId, String productName, 
                                    Integer cantidad, Integer regProd, BigDecimal precioUnitario, 
                                    BigDecimal subtotal, String claveCompuesta) {
        this.servicioReparacionId = servicioReparacionId;
        this.productId = productId;
        this.productName = productName;
        this.cantidad = cantidad;
        this.regProd = regProd;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.claveCompuesta = claveCompuesta;
    }

    // Getters y Setters
    public String getServicioReparacionId() { return servicioReparacionId; }
    public void setServicioReparacionId(String servicioReparacionId) { this.servicioReparacionId = servicioReparacionId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getRegProd() { return regProd; }
    public void setRegProd(Integer regProd) { this.regProd = regProd; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getClaveCompuesta() { return claveCompuesta; }
    public void setClaveCompuesta(String claveCompuesta) { this.claveCompuesta = claveCompuesta; }
}
