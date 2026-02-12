package com.inventory.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipo_evento_id", nullable = false)
    private TipoEvento tipoEvento;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_name", columnDefinition = "TEXT")
    private String productName;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Column(name = "cantidad_final", nullable = false)
    private Integer cantidadFinal;

    @Column(name = "precio_inicial", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioInicial = BigDecimal.ZERO;

    @Column(name = "precio_final", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioFinal = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "usuario_username", nullable = false)
    private User usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(columnDefinition = "TEXT")
    private String referencia;

    // Constructores
    public Auditoria() {
    }

    public Auditoria(TipoEvento tipoEvento, String productId, String productName,
                     Integer cantidadInicial, Integer cantidadFinal,
                     BigDecimal precioInicial, BigDecimal precioFinal, String descripcion,
                     User usuario, String referencia) {
        this.tipoEvento = tipoEvento;
        this.productId = productId;
        this.productName = productName;
        this.cantidadInicial = cantidadInicial;
        this.cantidadFinal = cantidadFinal;
        this.precioInicial = precioInicial != null ? precioInicial : BigDecimal.ZERO;
        this.precioFinal = precioFinal != null ? precioFinal : BigDecimal.ZERO;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.referencia = referencia;
        this.fecha = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
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

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
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

    @Override
    public String toString() {
        return "Auditoria{" +
                "id=" + id +
                ", tipoEvento=" + tipoEvento +
                ", productId=" + productId +
                ", productName=" + productName +
                ", cantidadInicial=" + cantidadInicial +
                ", cantidadFinal=" + cantidadFinal +
                ", precioInicial=" + precioInicial +
                ", precioFinal=" + precioFinal +
                ", descripcion='" + descripcion + '\'' +
                ", usuario=" + usuario +
                ", fecha=" + fecha +
                ", referencia='" + referencia + '\'' +
                '}';
    }
}
