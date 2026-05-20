package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidad SERVICIO — dominio independiente del inventario físico.
 * Representa mano de obra, diagnósticos, mantenimientos, instalaciones, etc.
 * NO descuenta stock, NO genera movimientos de inventario.
 */
@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código único legible (ej. SRV-001, DIAG-01). */
    @Column(nullable = false, unique = true, length = 30)
    private String codigo;
 
    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    /** Precio base de referencia. Se puede sobrescribir en VentaDetalle. */
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase = BigDecimal.ZERO;

    /** Duración estimada en minutos (opcional, para planificación). */
    @Column(name = "duracion_estimada_minutos")
    private Integer duracionEstimadaMinutos;

    /** Días de garantía sobre el trabajo realizado. */
    @Column(name = "garantia_dias")
    private Integer garantiaDias = 30;

    /**
     * Categoría técnica del servicio.
     * Valores sugeridos: DIAGNOSTICO, MANTENIMIENTO, REPARACION, INSTALACION, REVISION, OTRO
     */
    @Column(name = "categoria_servicio", length = 50)
    private String categoriaServicio;

    /** Tipo de electrodoméstico al que aplica este servicio (FK a categorias_electrodomestico). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_electrodomestico_id", nullable = true)
    private CategoriaElectrodomestico categoriaElectrodomestico;

    @Column(nullable = false)
    private boolean activo = true;

    public Servicio() {}

    public Servicio(String codigo, String nombre, BigDecimal precioBase, String categoriaServicio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.categoriaServicio = categoriaServicio;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioBase() { return precioBase; }
    public void setPrecioBase(BigDecimal precioBase) { this.precioBase = precioBase; }

    public Integer getDuracionEstimadaMinutos() { return duracionEstimadaMinutos; }
    public void setDuracionEstimadaMinutos(Integer duracionEstimadaMinutos) { this.duracionEstimadaMinutos = duracionEstimadaMinutos; }

    public Integer getGarantiaDias() { return garantiaDias; }
    public void setGarantiaDias(Integer garantiaDias) { this.garantiaDias = garantiaDias; }

    public String getCategoriaServicio() { return categoriaServicio; }
    public void setCategoriaServicio(String categoriaServicio) { this.categoriaServicio = categoriaServicio; }

    public CategoriaElectrodomestico getCategoriaElectrodomestico() { return categoriaElectrodomestico; }
    public void setCategoriaElectrodomestico(CategoriaElectrodomestico categoriaElectrodomestico) { this.categoriaElectrodomestico = categoriaElectrodomestico; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return "Servicio{id=" + id + ", codigo='" + codigo + "', nombre='" + nombre + "'}";
    }
}
