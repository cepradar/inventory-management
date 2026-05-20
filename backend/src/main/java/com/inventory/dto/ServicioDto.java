package com.inventory.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inventory.model.Servicio;

import java.math.BigDecimal;

public class ServicioDto {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioBase;
    private Integer duracionEstimadaMinutos;
    private Integer garantiaDias;
    private String categoriaServicio;
    private Long categoriaElectrodomesticoId;
    private String categoriaElectrodomesticoNombre;
    private boolean activo;

    public ServicioDto() {}

    /** Constructor para deserialización JSON desde el cliente. */
    @JsonCreator
    public ServicioDto(
            @JsonProperty("id")                           Long id,
            @JsonProperty("codigo")                       String codigo,
            @JsonProperty("nombre")                       String nombre,
            @JsonProperty("descripcion")                  String descripcion,
            @JsonProperty("precioBase")                   BigDecimal precioBase,
            @JsonProperty("duracionEstimadaMinutos")      Integer duracionEstimadaMinutos,
            @JsonProperty("garantiaDias")                 Integer garantiaDias,
            @JsonProperty("categoriaServicio")            String categoriaServicio,
            @JsonProperty("categoriaElectrodomesticoId")  Long categoriaElectrodomesticoId,
            @JsonProperty("categoriaElectrodomesticoNombre") String categoriaElectrodomesticoNombre,
            @JsonProperty("activo")                       boolean activo) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
        this.duracionEstimadaMinutos = duracionEstimadaMinutos;
        this.garantiaDias = garantiaDias;
        this.categoriaServicio = categoriaServicio;
        this.categoriaElectrodomesticoId = categoriaElectrodomesticoId;
        this.categoriaElectrodomesticoNombre = categoriaElectrodomesticoNombre;
        this.activo = activo;
    }

    /** Constructor desde entidad → respuesta API. */
    public ServicioDto(Servicio s) {
        this.id = s.getId();
        this.codigo = s.getCodigo();
        this.nombre = s.getNombre();
        this.descripcion = s.getDescripcion();
        this.precioBase = s.getPrecioBase();
        this.duracionEstimadaMinutos = s.getDuracionEstimadaMinutos();
        this.garantiaDias = s.getGarantiaDias();
        this.categoriaServicio = s.getCategoriaServicio();
        if (s.getCategoriaElectrodomestico() != null) {
            this.categoriaElectrodomesticoId = s.getCategoriaElectrodomestico().getId();
            this.categoriaElectrodomesticoNombre = s.getCategoriaElectrodomestico().getNombre();
        }
        this.activo = s.isActivo();
    }

    /** Convierte este DTO a entidad (para crear/actualizar). */
    public static Servicio toServicio(ServicioDto dto) {
        Servicio s = new Servicio();
        if (dto.getId() != null) s.setId(dto.getId());
        s.setCodigo(dto.getCodigo());
        s.setNombre(dto.getNombre());
        s.setDescripcion(dto.getDescripcion());
        s.setPrecioBase(dto.getPrecioBase() != null ? dto.getPrecioBase() : BigDecimal.ZERO);
        s.setDuracionEstimadaMinutos(dto.getDuracionEstimadaMinutos());
        s.setGarantiaDias(dto.getGarantiaDias() != null ? dto.getGarantiaDias() : 30);
        s.setCategoriaServicio(dto.getCategoriaServicio());
        s.setActivo(dto.isActivo());
        return s;
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

    public Long getCategoriaElectrodomesticoId() { return categoriaElectrodomesticoId; }
    public void setCategoriaElectrodomesticoId(Long categoriaElectrodomesticoId) { this.categoriaElectrodomesticoId = categoriaElectrodomesticoId; }

    public String getCategoriaElectrodomesticoNombre() { return categoriaElectrodomesticoNombre; }
    public void setCategoriaElectrodomesticoNombre(String categoriaElectrodomesticoNombre) { this.categoriaElectrodomesticoNombre = categoriaElectrodomesticoNombre; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
