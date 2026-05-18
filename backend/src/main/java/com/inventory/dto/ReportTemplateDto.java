package com.inventory.dto;

/**
 * DTO para transferencia de datos de ReportTemplate.
 */
public class ReportTemplateDto {

    private Long id;
    private String nombre;
    private String tipoReporte;
    private String descripcion;
    private boolean activo;
    private String fechaCreacion;
    private String fechaActualizacion;
    private String creadoPor;
    private boolean tieneJrxml;
    private boolean tieneJasper;
    private String archivoJrxmlNombre;
    private String archivoJasperNombre;
    private boolean esSistema;

    public ReportTemplateDto() {}

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(String tipoReporte) { this.tipoReporte = tipoReporte; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(String fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getCreadoPor() { return creadoPor; }
    public void setCreadoPor(String creadoPor) { this.creadoPor = creadoPor; }

    public boolean isTieneJrxml() { return tieneJrxml; }
    public void setTieneJrxml(boolean tieneJrxml) { this.tieneJrxml = tieneJrxml; }

    public boolean isTieneJasper() { return tieneJasper; }
    public void setTieneJasper(boolean tieneJasper) { this.tieneJasper = tieneJasper; }

    public String getArchivoJrxmlNombre() { return archivoJrxmlNombre; }
    public void setArchivoJrxmlNombre(String archivoJrxmlNombre) { this.archivoJrxmlNombre = archivoJrxmlNombre; }

    public String getArchivoJasperNombre() { return archivoJasperNombre; }
    public void setArchivoJasperNombre(String archivoJasperNombre) { this.archivoJasperNombre = archivoJasperNombre; }

    public boolean isEsSistema() { return esSistema; }
    public void setEsSistema(boolean esSistema) { this.esSistema = esSistema; }
}
