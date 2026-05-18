package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para gestionar las plantillas de reportes JasperReports.
 * Almacena metadata y rutas de archivos .jrxml y .jasper.
 *
 * Flujo de resolución de plantilla:
 *   1. Buscar plantilla activa en BD por (modulo + tipoDocumento)
 *   2. Si no existe, usar plantilla del sistema (classpath /reports/)
 */
@Entity
@Table(name = "report_templates")
public class ReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre descriptivo del reporte */
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Código único de la plantilla (ej: "factura_venta_v1").
     * Permite identificar la plantilla sin depender del ID.
     */
    @Column(unique = true, length = 100)
    private String codigo;

    /**
     * Módulo al que pertenece este reporte.
     * Usado para filtrar plantillas disponibles por módulo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "modulo", length = 50)
    private ModuloReporte modulo;

    /**
     * Tipo de documento que genera esta plantilla.
     * Define la categoría documental (factura, orden, kardex, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 60)
    private TipoDocumento tipoDocumento;

    /**
     * Campo legacy mantenido para compatibilidad.
     * Para nuevas integraciones usar tipoDocumento + modulo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reporte", length = 50)
    private TipoReporte tipoReporte;

    /** Versión del reporte (incrementar al actualizar la plantilla) */
    @Column(length = 20)
    private String version = "1.0";

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

    // ── Enums ─────────────────────────────────────────────────────────────────

    /** Módulos funcionales del sistema */
    public enum ModuloReporte {
        VENTAS,
        ORDENES,
        INVENTARIO,
        CLIENTES,
        REPORTES,
        GENERAL
    }

    /**
     * Tipos documentales soportados por el sistema.
     * Cada tipo mapea a un nombre de plantilla en el classpath (ej: FACTURA_VENTA → factura).
     */
    public enum TipoDocumento {
        FACTURA_VENTA("factura"),
        ORDEN_SERVICIO("orden_servicio"),
        ORDEN_INGRESO("orden_ingreso"),
        COTIZACION("cotizacion"),
        RECIBO("recibo"),
        KARDEX("kardex"),
        REPORTE_STOCK("reporte_stock"),
        ESTADO_CUENTA("estado_cuenta"),
        HISTORIAL_COMPRAS("historial_compras"),
        GENERICO("generico");

        /** Nombre de la plantilla del sistema en /resources/reports/ */
        private final String templateName;

        TipoDocumento(String templateName) {
            this.templateName = templateName;
        }

        public String getTemplateName() {
            return templateName;
        }
    }

    /** Enum legacy mantenido para compatibilidad */
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

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public ModuloReporte getModulo() { return modulo; }
    public void setModulo(ModuloReporte modulo) { this.modulo = modulo; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public TipoReporte getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(TipoReporte tipoReporte) { this.tipoReporte = tipoReporte; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

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
