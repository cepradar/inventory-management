package com.inventory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ElectrodomesticoDto {
    
    private Long id;
    private Long clienteId;
    private String clienteNombre;
    private String clienteApellido;
    private String tipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private String colorOFinish;
    private String estadoActual;
    private LocalDate fechaAdquisicion;
    private LocalDateTime fechaRegistro;
    private Boolean garantiaVigente;
    private LocalDate fechaVencimientoGarantia;
    private String notas;
    private String usuarioUsername;
    private String usuarioNombre;
    
    // Constructores
    public ElectrodomesticoDto() {
    }
    
    public ElectrodomesticoDto(Long id, Long clienteId, String tipo, String marca, String modelo, String estadoActual) {
        this.id = id;
        this.clienteId = clienteId;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.estadoActual = estadoActual;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getClienteNombre() {
        return clienteNombre;
    }
    
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }
    
    public String getClienteApellido() {
        return clienteApellido;
    }
    
    public void setClienteApellido(String clienteApellido) {
        this.clienteApellido = clienteApellido;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getMarca() {
        return marca;
    }
    
    public void setMarca(String marca) {
        this.marca = marca;
    }
    
    public String getModelo() {
        return modelo;
    }
    
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    
    public String getNumeroSerie() {
        return numeroSerie;
    }
    
    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }
    
    public String getColorOFinish() {
        return colorOFinish;
    }
    
    public void setColorOFinish(String colorOFinish) {
        this.colorOFinish = colorOFinish;
    }
    
    public String getEstadoActual() {
        return estadoActual;
    }
    
    public void setEstadoActual(String estadoActual) {
        this.estadoActual = estadoActual;
    }
    
    public LocalDate getFechaAdquisicion() {
        return fechaAdquisicion;
    }
    
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) {
        this.fechaAdquisicion = fechaAdquisicion;
    }
    
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
    
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
    
    public Boolean getGarantiaVigente() {
        return garantiaVigente;
    }
    
    public void setGarantiaVigente(Boolean garantiaVigente) {
        this.garantiaVigente = garantiaVigente;
    }
    
    public LocalDate getFechaVencimientoGarantia() {
        return fechaVencimientoGarantia;
    }
    
    public void setFechaVencimientoGarantia(LocalDate fechaVencimientoGarantia) {
        this.fechaVencimientoGarantia = fechaVencimientoGarantia;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
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
    
    @Override
    public String toString() {
        return "ElectrodomesticoDto{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", estadoActual='" + estadoActual + '\'' +
                '}';
    }
}
