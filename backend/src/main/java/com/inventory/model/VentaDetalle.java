package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Línea de detalle de una venta.
 * Soporta PRODUCTOS físicos (con descuento de inventario) y SERVICIOS técnicos (sin inventario).
 *
 * Discriminador: tipo_item = PRODUCTO | SERVICIO
 *   - Si tipo_item = PRODUCTO → product != null, servicio == null
 *   - Si tipo_item = SERVICIO → servicio != null, product == null
 */
@Entity
@Table(name = "venta_detalle")
public class VentaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    /** Referencia al producto físico (nullable cuando el ítem es un servicio). */
    @ManyToOne(optional = true)
    @JoinColumn(name = "product_id", nullable = true)
    private Product product;

    /** Referencia al servicio técnico (nullable cuando el ítem es un producto). */
    @ManyToOne(optional = true)
    @JoinColumn(name = "servicio_id", nullable = true)
    private Servicio servicio;

    /**
     * Tipo de ítem: PRODUCTO o SERVICIO.
     * Discriminador para saber qué FK usar.
     */
    @Column(name = "tipo_item", length = 20)
    private String tipoItem = "PRODUCTO";

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal subtotal;

    public VentaDetalle() {}

    /** Constructor para líneas de PRODUCTO físico. */
    public VentaDetalle(Venta venta, Product product, Integer cantidad, BigDecimal precioUnitario) {
        this.venta = venta;
        this.product = product;
        this.tipoItem = "PRODUCTO";
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }

    /** Constructor para líneas de SERVICIO técnico. */
    public VentaDetalle(Venta venta, Servicio servicio, Integer cantidad, BigDecimal precioUnitario) {
        this.venta = venta;
        this.servicio = servicio;
        this.tipoItem = "SERVICIO";
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public String getTipoItem() { return tipoItem; }
    public void setTipoItem(String tipoItem) { this.tipoItem = tipoItem; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}