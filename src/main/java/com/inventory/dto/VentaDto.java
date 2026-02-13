package com.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public class VentaDto {
    private Long id;
    private BigDecimal totalVenta;
    private String nombreComprador;
    private String telefonoComprador;
    private String emailComprador;
    private String usuarioUsername;
    private String usuarioNombre;
    private LocalDateTime fecha;
    private String observaciones;
    private List<VentaDetalleDto> detalles;

    public VentaDto() {}

    public VentaDto(Long id, BigDecimal totalVenta, String nombreComprador, String telefonoComprador,
                    String emailComprador, String usuarioUsername, String usuarioNombre,
                    LocalDateTime fecha, String observaciones, List<VentaDetalleDto> detalles) {
        this.id = id;
        this.totalVenta = totalVenta;
        this.nombreComprador = nombreComprador;
        this.telefonoComprador = telefonoComprador;
        this.emailComprador = emailComprador;
        this.usuarioUsername = usuarioUsername;
        this.usuarioNombre = usuarioNombre;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.detalles = detalles;
    }

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getTotalVenta() { return totalVenta; }
    public void setTotalVenta(BigDecimal totalVenta) { this.totalVenta = totalVenta; }

    public String getNombreComprador() { return nombreComprador; }
    public void setNombreComprador(String nombreComprador) { this.nombreComprador = nombreComprador; }

    public String getTelefonoComprador() { return telefonoComprador; }
    public void setTelefonoComprador(String telefonoComprador) { this.telefonoComprador = telefonoComprador; }

    public String getEmailComprador() { return emailComprador; }
    public void setEmailComprador(String emailComprador) { this.emailComprador = emailComprador; }

    public String getUsuarioUsername() { return usuarioUsername; }
    public void setUsuarioUsername(String usuarioUsername) { this.usuarioUsername = usuarioUsername; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public List<VentaDetalleDto> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalleDto> detalles) { this.detalles = detalles; }
}