package com.inventory.dto;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public class VentaDto {
    private Long id;
    private String productId;
    private String productNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal totalVenta;
    private String nombreComprador;
    private String telefonoComprador;
    private String emailComprador;
    private String usuarioUsername;
    private String usuarioNombre;
    private LocalDateTime fecha;
    private String observaciones;

    // Constructores
    public VentaDto() {
    }

    public VentaDto(Long id, String productId, String productNombre, Integer cantidad, 
                    BigDecimal precioUnitario, BigDecimal totalVenta, String nombreComprador, 
                    String telefonoComprador, String emailComprador, String usuarioUsername, 
                    String usuarioNombre, LocalDateTime fecha, String observaciones) {
        this.id = id;
        this.productId = productId;
        this.productNombre = productNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalVenta = totalVenta;
        this.nombreComprador = nombreComprador;
        this.telefonoComprador = telefonoComprador;
        this.emailComprador = emailComprador;
        this.usuarioUsername = usuarioUsername;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "VentaDto{" +
                "id=" + id +
                ", productId=" + productId +
                ", productNombre='" + productNombre + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", totalVenta=" + totalVenta +
                ", nombreComprador='" + nombreComprador + '\'' +
                ", telefonoComprador='" + telefonoComprador + '\'' +
                ", emailComprador='" + emailComprador + '\'' +
                ", usuarioUsername='" + usuarioUsername + '\'' +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", fecha=" + fecha +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
