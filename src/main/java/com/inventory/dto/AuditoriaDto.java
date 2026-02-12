package com.inventory.dto;

import com.inventory.model.Auditoria;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditoriaDto {
    
    private Long id;
    private String tipoEventoId;
    private String tipoEventoNombre;
    private String tipoEventoCategoria; // 🔔 Nueva propiedad
    private String productId;
    private String productName;
    private Integer cantidadInicial;
    private Integer cantidadFinal;
    private BigDecimal precioInicial;
    private BigDecimal precioFinal;
    private String descripcion;
    private String usuarioUsername;
    private String usuarioNombreCompleto;
    private LocalDateTime fecha;
    private String referencia;

    // Constructor vacío
    public AuditoriaDto() {
    }

    // Constructor completo
    public AuditoriaDto(Long id, String tipoEventoId, String tipoEventoNombre, String productId,
                        String productName, Integer cantidadInicial, Integer cantidadFinal,
                        BigDecimal precioInicial, BigDecimal precioFinal,
                        String descripcion, String usuarioUsername,
                        String usuarioNombreCompleto, LocalDateTime fecha, String referencia) {
        this.id = id;
        this.tipoEventoId = tipoEventoId;
        this.tipoEventoNombre = tipoEventoNombre;
        this.productId = productId;
        this.productName = productName;
        this.cantidadInicial = cantidadInicial;
        this.cantidadFinal = cantidadFinal;
        this.precioInicial = precioInicial;
        this.precioFinal = precioFinal;
        this.descripcion = descripcion;
        this.usuarioUsername = usuarioUsername;
        this.usuarioNombreCompleto = usuarioNombreCompleto;
        this.fecha = fecha;
        this.referencia = referencia;
    }

    // Constructor desde entidad
    public AuditoriaDto(Auditoria auditoria) {
        this.id = auditoria.getId();
        
        // Manejo seguro de TipoEvento
        if (auditoria.getTipoEvento() != null) {
            this.tipoEventoId = auditoria.getTipoEvento().getId();
            this.tipoEventoNombre = auditoria.getTipoEvento().getNombre();
            
            // Manejo seguro de Categoria
            if (auditoria.getTipoEvento().getCategoria() != null) {
                this.tipoEventoCategoria = auditoria.getTipoEvento().getCategoria().getNombre();
            } else {
                this.tipoEventoCategoria = "SIN_CATEGORIA";
            }
        } else {
            this.tipoEventoId = "DESCONOCIDO";
            this.tipoEventoNombre = "Desconocido";
            this.tipoEventoCategoria = "SIN_CATEGORIA";
        }
        
        this.productId = auditoria.getProductId();
        this.productName = auditoria.getProductName();
        this.cantidadInicial = auditoria.getCantidadInicial();
        this.cantidadFinal = auditoria.getCantidadFinal();
        this.precioInicial = auditoria.getPrecioInicial();
        this.precioFinal = auditoria.getPrecioFinal();
        this.descripcion = auditoria.getDescripcion();
        this.usuarioUsername = auditoria.getUsuario() != null ? auditoria.getUsuario().getUsername() : "Desconocido";
        this.usuarioNombreCompleto = auditoria.getUsuario() != null 
                ? auditoria.getUsuario().getFirstName() + " " + auditoria.getUsuario().getLastName()
                : "Desconocido";
        this.fecha = auditoria.getFecha();
        this.referencia = auditoria.getReferencia();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoEventoId() {
        return tipoEventoId;
    }

    public void setTipoEventoId(String tipoEventoId) {
        this.tipoEventoId = tipoEventoId;
    }

    public String getTipoEventoNombre() {
        return tipoEventoNombre;
    }

    public void setTipoEventoNombre(String tipoEventoNombre) {
        this.tipoEventoNombre = tipoEventoNombre;
    }

    public String getTipoEventoCategoria() {
        return tipoEventoCategoria;
    }

    public void setTipoEventoCategoria(String tipoEventoCategoria) {
        this.tipoEventoCategoria = tipoEventoCategoria;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(Integer cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public Integer getCantidadFinal() {
        return cantidadFinal;
    }

    public void setCantidadFinal(Integer cantidadFinal) {
        this.cantidadFinal = cantidadFinal;
    }

    public BigDecimal getPrecioInicial() {
        return precioInicial;
    }

    public void setPrecioInicial(BigDecimal precioInicial) {
        this.precioInicial = precioInicial;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuarioUsername() {
        return usuarioUsername;
    }

    public void setUsuarioUsername(String usuarioUsername) {
        this.usuarioUsername = usuarioUsername;
    }

    public String getUsuarioNombreCompleto() {
        return usuarioNombreCompleto;
    }

    public void setUsuarioNombreCompleto(String usuarioNombreCompleto) {
        this.usuarioNombreCompleto = usuarioNombreCompleto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
