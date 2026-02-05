package com.inventory.dto;

import com.inventory.model.Auditoria;
import java.time.LocalDateTime;

public class AuditoriaDto {
    
    private Long id;
    private String tipoEventoId;
    private String tipoEventoNombre;
    private String tipoEventoCategoria; // 🔔 Nueva propiedad
    private Long productId;
    private String productName;
    private Integer cantidad;
    private String descripcion;
    private String usuarioUsername;
    private String usuarioNombreCompleto;
    private LocalDateTime fecha;
    private String referencia;

    // Constructor vacío
    public AuditoriaDto() {
    }

    // Constructor completo
    public AuditoriaDto(Long id, String tipoEventoId, String tipoEventoNombre, Long productId, 
                        String productName, Integer cantidad, String descripcion, String usuarioUsername,
                        String usuarioNombreCompleto, LocalDateTime fecha, String referencia) {
        this.id = id;
        this.tipoEventoId = tipoEventoId;
        this.tipoEventoNombre = tipoEventoNombre;
        this.productId = productId;
        this.productName = productName;
        this.cantidad = cantidad;
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
        
        this.productId = auditoria.getProduct() != null ? Long.parseLong(auditoria.getProduct().getId()) : null;
        this.productName = auditoria.getProduct() != null ? auditoria.getProduct().getName() : "[Producto eliminado]";
        this.cantidad = auditoria.getCantidad();
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
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
