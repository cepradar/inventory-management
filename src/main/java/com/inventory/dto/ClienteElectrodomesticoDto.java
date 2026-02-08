package com.inventory.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClienteElectrodomesticoDto {
    private Long id;
    private String clienteId;
    private String clienteTipoDocumentoId;
    private String clienteNombre;
    private String clienteTelefono;
    private String electrodomesticoTipo;
    private Long marcaElectrodomesticoId;
    private String marcaElectrodomesticoNombre;
    private String electrodomesticoModelo;
    private String numeroSerie;
    private String colorOFinish;
    private String estado;
    private LocalDate fechaAdquisicion;
    private LocalDateTime fechaRegistro;
    private Boolean garantiaVigente;
    private LocalDate fechaVencimientoGarantia;
    private String notas;
    private String usuarioUsername;
    private String usuarioNombre;
    
    public ClienteElectrodomesticoDto() {}
    
    public ClienteElectrodomesticoDto(Long id, String clienteId, String clienteNombre,
                                      String electrodomesticoTipo, String numeroSerie, String estado) {
        this.id = id;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.electrodomesticoTipo = electrodomesticoTipo;
        this.numeroSerie = numeroSerie;
        this.estado = estado;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getClienteTipoDocumentoId() { return clienteTipoDocumentoId; }
    public void setClienteTipoDocumentoId(String clienteTipoDocumentoId) { this.clienteTipoDocumentoId = clienteTipoDocumentoId; }
    
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    
    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }
    
    public String getElectrodomesticoTipo() { return electrodomesticoTipo; }
    public void setElectrodomesticoTipo(String electrodomesticoTipo) { this.electrodomesticoTipo = electrodomesticoTipo; }
    
    public Long getMarcaElectrodomesticoId() { return marcaElectrodomesticoId; }
    public void setMarcaElectrodomesticoId(Long marcaElectrodomesticoId) { this.marcaElectrodomesticoId = marcaElectrodomesticoId; }
    
    public String getMarcaElectrodomesticoNombre() { return marcaElectrodomesticoNombre; }
    public void setMarcaElectrodomesticoNombre(String marcaElectrodomesticoNombre) { this.marcaElectrodomesticoNombre = marcaElectrodomesticoNombre; }
    
    public String getElectrodomesticoModelo() { return electrodomesticoModelo; }
    public void setElectrodomesticoModelo(String electrodomesticoModelo) { this.electrodomesticoModelo = electrodomesticoModelo; }
    
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    
    public String getColorOFinish() { return colorOFinish; }
    public void setColorOFinish(String colorOFinish) { this.colorOFinish = colorOFinish; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Boolean getGarantiaVigente() { return garantiaVigente; }
    public void setGarantiaVigente(Boolean garantiaVigente) { this.garantiaVigente = garantiaVigente; }
    
    public LocalDate getFechaVencimientoGarantia() { return fechaVencimientoGarantia; }
    public void setFechaVencimientoGarantia(LocalDate fechaVencimientoGarantia) { this.fechaVencimientoGarantia = fechaVencimientoGarantia; }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public String getUsuarioUsername() { return usuarioUsername; }
    public void setUsuarioUsername(String usuarioUsername) { this.usuarioUsername = usuarioUsername; }
    
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
}
