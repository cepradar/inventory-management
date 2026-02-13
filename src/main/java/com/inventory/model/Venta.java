package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cambia de Product a lista de VentaDetalle
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VentaDetalle> detalles;

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

    public Venta(List<VentaDetalle> detalles, String nombreComprador, 
                 String telefonoComprador, String emailComprador, User usuario, String observaciones) {
        this.detalles = detalles;
        this.totalVenta = calcularTotalVenta();
        this.nombreComprador = nombreComprador;
        this.telefonoComprador = telefonoComprador;
        this.emailComprador = emailComprador;
        this.usuario = usuario;
        this.observaciones = observaciones;
        this.fecha = LocalDateTime.now();
    }

    private BigDecimal calcularTotalVenta() {
        if (detalles == null) return BigDecimal.ZERO;
        return detalles.stream()
                .map(VentaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<VentaDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<VentaDetalle> detalles) {
        this.detalles = detalles;
        this.totalVenta = calcularTotalVenta();
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
                ", detalles=" + detalles +
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