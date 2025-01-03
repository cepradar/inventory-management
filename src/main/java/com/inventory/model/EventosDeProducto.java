package com.inventory.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventos_producto")
public class EventosDeProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_username", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipoDeEvento_id", nullable = false)
    private TiposDeEvento tipoDeEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Productos producto;

    @Column(nullable = false)
    private LocalDateTime fechaEvento;

    // Constructor vacío requerido por JPA
    public EventosDeProducto() {}

    public EventosDeProducto(Long id, Usuarios usuario, TiposDeEvento tipoDeEvento, Productos producto,
            LocalDateTime fechaEvento) {
        this.id = id;
        this.usuario = usuario;
        this.tipoDeEvento = tipoDeEvento;
        this.producto = producto;
        this.fechaEvento = fechaEvento;
    }



    public Usuarios getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }

    public TiposDeEvento getTipoDeEvento() {
        return tipoDeEvento;
    }

    public void setTipoDeEvento(TiposDeEvento tipoDeEvento) {
        this.tipoDeEvento = tipoDeEvento;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }
}
