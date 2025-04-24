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

    // Constructor vacío requerido por JPA
    public TipoEvento() {}

    public TipoEvento(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
}