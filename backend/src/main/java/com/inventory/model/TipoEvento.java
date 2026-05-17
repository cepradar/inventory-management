package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_evento")
public class TipoEvento {

    @Id
    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaTipoEvento categoria;

    // Constructor vacío requerido por JPA
    public TipoEvento() {}

    public TipoEvento(String id, String nombre, CategoriaTipoEvento categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaTipoEvento getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTipoEvento categoria) {
        this.categoria = categoria;
    }
}
