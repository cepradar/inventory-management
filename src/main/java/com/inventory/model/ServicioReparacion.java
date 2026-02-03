package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orden_de_servicio")
public class ServicioReparacion {
    
    @Id
    @Column(length = 6, nullable = false, unique = true)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "cliente_electrodomestico_id", nullable = false)
    private ClienteElectrodomestico clienteElectrodomestico;
    
    @OneToMany(mappedBy = "servicioReparacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServicioReparacionProducto> productos = new ArrayList<>();
    
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
    private String estado = "RECIBIDO"; // RECIBIDO, EN_DIAGNOSTICO, EN_REPARACION, LISTO, ENTREGADO, CANCELADO
    
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
    
    @Column(columnDefinition = "TEXT")
    private String observaciones;
    
    // Constructores
    public ServicioReparacion() {
        this.fechaIngreso = LocalDateTime.now();
        this.estado = "RECIBIDO";
        this.garantiaServicio = 30;
        this.costoServicio = BigDecimal.ZERO;
        this.costoRepuestos = BigDecimal.ZERO;
        this.totalCosto = BigDecimal.ZERO;
    }
    
    public ServicioReparacion(Cliente cliente, ClienteElectrodomestico clienteElectrodomestico, 
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
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<ServicioReparacionProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<ServicioReparacionProducto> productos) {
        this.productos = productos;
    }

    public void agregarProducto(ServicioReparacionProducto producto) {
        productos.add(producto);
        producto.setServicioReparacion(this);
    }

    public void removerProducto(ServicioReparacionProducto producto) {
        productos.remove(producto);
        producto.setServicioReparacion(null);
    }
    
    @Override
    public String toString() {
        return "ServicioReparacion{" +
                "id=" + id +
                ", tipoServicio='" + tipoServicio + '\'' +
                ", estado='" + estado + '\'' +
                ", totalCosto=" + totalCosto +
                ", fechaIngreso=" + fechaIngreso +
                ", garantiaServicio=" + garantiaServicio +
                '}';
    }
}
