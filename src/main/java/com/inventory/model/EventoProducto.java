package com.inventory.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventos_producto")
public class EventoProducto {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_username", nullable = false)
    private User usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipoDeEvento_id", nullable = false)
    private TipoEvento tipoDeEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Product producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "cliente_id"),
        @JoinColumn(name = "cliente_tipo_documento")
    })
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime fechaEvento;
    
    private Integer cantidad;
    private String observacion;

    // Constructor vacío requerido por JPA
    public EventoProducto() {}

    public EventoProducto(String id, User usuario, TipoEvento tipoDeEvento, Product producto,
            LocalDateTime fechaEvento) {
        this.id = id;
        this.usuario = usuario;
        this.tipoDeEvento = tipoDeEvento;
        this.producto = producto;
        this.fechaEvento = fechaEvento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUsuario() {
        return usuario;
    }

    public void setUsuario(User usuario) {
        this.usuario = usuario;
    }

    public TipoEvento getTipoDeEvento() {
        return tipoDeEvento;
    }

    public void setTipoDeEvento(TipoEvento tipoDeEvento) {
        this.tipoDeEvento = tipoDeEvento;
    }

    public Product getProducto() {
        return producto;
    }

    public void setProducto(Product producto) {
        this.producto = producto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    
    
}
