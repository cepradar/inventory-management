package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_de_servicio")
public class OrdenDeServicio {
    
    @Id
    @Column(length = 6, nullable = false, unique = true)
    private String id;
    
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "cliente_id", nullable = false),
        @JoinColumn(name = "cliente_tipo_documento", nullable = false)
    })
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "cliente_electrodomestico_id", nullable = false)
    private ClienteElectrodomestico clienteElectrodomestico;
    
    // REFACTOR: Productos ahora se manejan en el módulo de Ventas
    // Una orden de servicio puede tener múltiples ventas asociadas
    // No guardar relación directa con productos aquí
    
    @Column(nullable = false)
    private String tipoServicio; // REPARACION, MANTENIMIENTO, DIAGNOSTICO
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcionProblema;
    
    @Column(columnDefinition = "TEXT")
    private String diagnostico;
    
    @Column(columnDefinition = "TEXT")
    private String solucion;
    
    @Column(columnDefinition = "TEXT", name = "partes_cambiadas")
    private String partesCambiadas;
    
    @Column(precision = 10, scale = 2, name = "costo_servicio")
    private BigDecimal costoServicio = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2, name = "costo_repuestos")
    private BigDecimal costoRepuestos = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2, name = "total_costo")
    private BigDecimal totalCosto = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private String estado = "SOC"; // Código de tipo_evento (categoria ORDEN)
    
    @Column(name = "fecha_ingreso", nullable = false, updatable = false)
    private LocalDateTime fechaIngreso;
    
    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;
    
    @Column(name = "garantia_servicio")
    private Integer garantiaServicio = 30; // Días de garantía en la reparación (default 30)
    
    @Column(name = "vencimiento_garantia")
    private LocalDate vencimientoGarantia;
    
    @ManyToOne
    @JoinColumn(name = "usuario_username", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_asignado_username")
    private User tecnicoAsignado; // Técnico asignado para realizar el servicio

    @Column(name = "fecha_asignacion")
    private LocalDateTime fechaAsignacion;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // Constructores
    public OrdenDeServicio() {
        this.fechaIngreso = LocalDateTime.now();
        this.estado = "SOC";
        this.garantiaServicio = 30;
        this.costoServicio = BigDecimal.ZERO;
        this.costoRepuestos = BigDecimal.ZERO;
        this.totalCosto = BigDecimal.ZERO;
    }
    
    public OrdenDeServicio(Cliente cliente, ClienteElectrodomestico clienteElectrodomestico, 
                              String tipoServicio, String descripcionProblema, User usuario) {
        this();
        this.cliente = cliente;
        this.clienteElectrodomestico = clienteElectrodomestico;
        this.tipoServicio = tipoServicio;
        this.descripcionProblema = descripcionProblema;
        this.usuario = usuario;
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public ClienteElectrodomestico getClienteElectrodomestico() {
        return clienteElectrodomestico;
    }
    
    public void setClienteElectrodomestico(ClienteElectrodomestico clienteElectrodomestico) {
        this.clienteElectrodomestico = clienteElectrodomestico;
    }
    
    public String getTipoServicio() {
        return tipoServicio;
    }
    
    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
    
    public String getDescripcionProblema() {
        return descripcionProblema;
    }
    
    public void setDescripcionProblema(String descripcionProblema) {
        this.descripcionProblema = descripcionProblema;
    }
    
    public String getDiagnostico() {
        return diagnostico;
    }
    
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }
    
    public String getSolucion() {
        return solucion;
    }
    
    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }
    
    public String getPartesCambiadas() {
        return partesCambiadas;
    }
    
    public void setPartesCambiadas(String partesCambiadas) {
        this.partesCambiadas = partesCambiadas;
    }
    
    public BigDecimal getCostoServicio() {
        return costoServicio;
    }
    
    public void setCostoServicio(BigDecimal costoServicio) {
        this.costoServicio = costoServicio;
        recalcularTotal();
    }
    
    public BigDecimal getCostoRepuestos() {
        return costoRepuestos;
    }
    
    public void setCostoRepuestos(BigDecimal costoRepuestos) {
        this.costoRepuestos = costoRepuestos;
        recalcularTotal();
    }
    
    public BigDecimal getTotalCosto() {
        return totalCosto;
    }
    
    public void setTotalCosto(BigDecimal totalCosto) {
        this.totalCosto = totalCosto;
    }
    
    private void recalcularTotal() {
        this.totalCosto = (this.costoServicio != null ? this.costoServicio : BigDecimal.ZERO)
                .add(this.costoRepuestos != null ? this.costoRepuestos : BigDecimal.ZERO);
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }
    
    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }
    
    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
    
    public Integer getGarantiaServicio() {
        return garantiaServicio;
    }
    
    public void setGarantiaServicio(Integer garantiaServicio) {
        this.garantiaServicio = garantiaServicio;
    }
    
    public LocalDate getVencimientoGarantia() {
        return vencimientoGarantia;
    }
    
    public void setVencimientoGarantia(LocalDate vencimientoGarantia) {
        this.vencimientoGarantia = vencimientoGarantia;
    }
    
    public User getUsuario() {
        return usuario;
    }
    
    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public User getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(User tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    @Override
    public String toString() {
        return "OrdenDeServicio{" +
                "id=" + id +
                ", tipoServicio='" + tipoServicio + '\'' +
                ", estado='" + estado + '\'' +
                ", totalCosto=" + totalCosto +
                ", fechaIngreso=" + fechaIngreso +
                ", garantiaServicio=" + garantiaServicio +
                '}';
    }
}
