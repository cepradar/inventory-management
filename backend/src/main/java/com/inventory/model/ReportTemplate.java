package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para gestionar las plantillas de reportes JasperReports.
 * Almacena metadata y rutas de archivos .jrxml y .jasper.
 */
@Entity
@Table(name = "report_templates")
public class ReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private TipoReporte tipoReporte;

    /** Nombre del archivo .jrxml almacenado (solo nombre, sin ruta completa) */
    @Column(name = "archivo_jrxml_nombre", length = 300)
    private String archivoJrxmlNombre;

    /** Nombre del archivo .jasper almacenado (solo nombre, sin ruta completa) */
    @Column(name = "archivo_jasper_nombre", length = 300)
    private String archivoJasperNombre;

    @Column(length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @Column(name = "creado_por", length = 100)
    private String creadoPor;

    /** Indica si es una plantilla del sistema (no editable/eliminable por el usuario) */
    @Column(name = "es_sistema", nullable = false)
    private Boolean esSistema = false;

    public enum TipoReporte {
        FACTURA,
        ORDEN_SERVICIO,
        INVENTARIO,
        TECNICO,
        OTRO
    }

    public ReportTemplate() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoReporte getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(TipoReporte tipoReporte) { this.tipoReporte = tipoReporte; }

    public String getArchivoJrxmlNombre() { return archivoJrxmlNombre; }
    public void setArchivoJrxmlNombre(String archivoJrxmlNombre) { this.archivoJrxmlNombre = archivoJrxmlNombre; }

    public String getArchivoJasperNombre() { return archivoJasperNombre; }
    public void setArchivoJasperNombre(String archivoJasperNombre) { this.archivoJasperNombre = archivoJasperNombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

    public Boolean getEsSistema() { return esSistema; }
    public void setEsSistema(Boolean esSistema) { this.esSistema = esSistema; }
}
