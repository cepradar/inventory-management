package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private BigDecimal precioUnitario;

    @Column(nullable = false)
    private BigDecimal totalVenta;

    @Column(nullable = false)
    private String nombreComprador;

    @Column
    private String telefonoComprador;

    @Column
    private String emailComprador;

    @ManyToOne
    @JoinColumn(name = "usuario_username", nullable = false)
    private User usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column
    private String observaciones;

    // Constructores
    public Venta() {
    }

    public Venta(Product product, Integer cantidad, BigDecimal precioUnitario, String nombreComprador, 
                 String telefonoComprador, String emailComprador, User usuario, String observaciones) {
        this.product = product;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalVenta = precioUnitario.multiply(new BigDecimal(cantidad));
        this.nombreComprador = nombreComprador;
        this.telefonoComprador = telefonoComprador;
        this.emailComprador = emailComprador;
        this.usuario = usuario;
        this.observaciones = observaciones;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(BigDecimal totalVenta) {
        this.totalVenta = totalVenta;
    }

    public String getNombreComprador() {
        return nombreComprador;
    }

    public void setNombreComprador(String nombreComprador) {
        this.nombreComprador = nombreComprador;
    }

    public String getTelefonoComprador() {
        return telefonoComprador;
    }

    public void setTelefonoComprador(String telefonoComprador) {
        this.telefonoComprador = telefonoComprador;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Venta{" +
                "id=" + id +
                ", product=" + product +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", totalVenta=" + totalVenta +
                ", nombreComprador='" + nombreComprador + '\'' +
                ", telefonoComprador='" + telefonoComprador + '\'' +
                ", emailComprador='" + emailComprador + '\'' +
                ", usuario=" + usuario +
                ", fecha=" + fecha +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
