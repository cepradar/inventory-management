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

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VentaDetalle> detalles;

    /**
     * FK compuesta hacia la tabla clientes (id + tipo_documento).
     * Reemplaza los campos legacy nombreComprador / telefonoComprador / emailComprador.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "cliente_id",             referencedColumnName = "id"),
        @JoinColumn(name = "cliente_tipo_documento", referencedColumnName = "tipo_documento")
    })
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_username", nullable = false)
    private User usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column
    private String observaciones;

    @Column(name = "orden_de_servicio_id", nullable = true)
    private String ordenDeServicioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "orden_de_servicio_id",
        referencedColumnName = "id",
        insertable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "fk_venta_orden_servicio")
    )
    private OrdenDeServicio ordenDeServicio;

    public Venta() {}

    private BigDecimal calcularTotalVenta() {
        if (detalles == null) return BigDecimal.ZERO;
        return detalles.stream()
                .map(VentaDetalle::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ── Getters / Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<VentaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<VentaDetalle> detalles) {
        this.detalles = detalles;
    }

    public BigDecimal getTotalVenta() { return calcularTotalVenta(); }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getOrdenDeServicioId() { return ordenDeServicioId; }
    public void setOrdenDeServicioId(String ordenDeServicioId) { this.ordenDeServicioId = ordenDeServicioId; }

    public OrdenDeServicio getOrdenDeServicio() { return ordenDeServicio; }
    public void setOrdenDeServicio(OrdenDeServicio ordenDeServicio) { this.ordenDeServicio = ordenDeServicio; }

    @Override
    public String toString() {
        return "Venta{id=" + id
                + ", totalVenta=" + getTotalVenta()
                + ", cliente=" + (cliente != null ? cliente.getId() : "null")
                + ", usuario=" + (usuario != null ? usuario.getUsername() : "null")
                + ", fecha=" + fecha + "}";
    }
}