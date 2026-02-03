package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cliente_electrodomesticos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_serial_marca_cliente",
        columnNames = {"numero_serie", "marca_electrodomestico_id", "cliente_id"}
    )
)
public class ClienteElectrodomestico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(name = "electrodomestico_tipo")
    private String electrodomesticoTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_electrodomestico_id")
    private MarcaElectrodomestico marcaElectrodomestico;

    @Column(name = "electrodomestico_modelo")
    private String electrodomesticoModelo;
    
    @Column(nullable = false)
    private String numeroSerie; // Número de serie del electrodoméstico (puede repetirse en diferentes registros/fechas)
    
    private String colorOFinish;
    
    @Column(nullable = false)
    private String estado = "ACTIVO"; // ACTIVO, EN_REPARACION, INACTIVO, RETIRADO
    
    private LocalDate fechaAdquisicion;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    private Boolean garantiaVigente = false;
    private LocalDate fechaVencimientoGarantia;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_username", nullable = false)
    private User usuario;
    
    public ClienteElectrodomestico() {
        this.fechaRegistro = LocalDateTime.now();
        this.estado = "ACTIVO";
        this.garantiaVigente = false;
    }
    
    public ClienteElectrodomestico(Cliente cliente, String numeroSerie, User usuario) {
        this();
        this.cliente = cliente;
        this.numeroSerie = numeroSerie;
        this.usuario = usuario;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public String getElectrodomesticoTipo() {
        return electrodomesticoTipo;
    }

    public void setElectrodomesticoTipo(String electrodomesticoTipo) {
        this.electrodomesticoTipo = electrodomesticoTipo;
    }

    public MarcaElectrodomestico getMarcaElectrodomestico() {
        return marcaElectrodomestico;
    }

    public void setMarcaElectrodomestico(MarcaElectrodomestico marcaElectrodomestico) {
        this.marcaElectrodomestico = marcaElectrodomestico;
    }

    public String getElectrodomesticoModelo() {
        return electrodomesticoModelo;
    }

    public void setElectrodomesticoModelo(String electrodomesticoModelo) {
        this.electrodomesticoModelo = electrodomesticoModelo;
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
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
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
    
    public User getUsuario() {
        return usuario;
    }
    
    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }
    
    @Override
    public String toString() {
        return "ClienteElectrodomestico{" +
                "id=" + id +
                ", numeroSerie='" + numeroSerie + '\'' +
                ", estado='" + estado + '\'' +
                ", garantiaVigente=" + garantiaVigente +
                ", fechaVencimientoGarantia=" + fechaVencimientoGarantia +
                '}';
    }
}
