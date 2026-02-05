package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "orden_servicio_producto")
@IdClass(OrdenServicioProductoId.class)
public class OrdenServicioProducto {
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_de_servicio_id", nullable = false)
    private OrdenDeServicio servicioReparacion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product producto;
    
    @Column(nullable = false)
    private Integer cantidad = 1;

    @Id
    @Column(name = "reg_prod", nullable = false)
    private Integer regProd = 1;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal precioUnitario = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    // Constructores
    public OrdenServicioProducto() {
    }
    
    public OrdenServicioProducto(OrdenDeServicio servicioReparacion, Product producto, 
                                     Integer cantidad, BigDecimal precioUnitario) {
        this.servicioReparacion = servicioReparacion;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }
    
    // Getters y Setters
    public OrdenDeServicio getServicioReparacion() {
        return servicioReparacion;
    }
    
    public void setServicioReparacion(OrdenDeServicio servicioReparacion) {
        this.servicioReparacion = servicioReparacion;
    }
    
    public Product getProducto() {
        return producto;
    }
    
    public void setProducto(Product producto) {
        this.producto = producto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getRegProd() {
        return regProd;
    }

    public void setRegProd(Integer regProd) {
        this.regProd = regProd;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
