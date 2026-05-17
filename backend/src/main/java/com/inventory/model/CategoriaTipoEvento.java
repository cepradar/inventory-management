package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_tipo_evento")
public class CategoriaTipoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    public CategoriaTipoEvento() {}

    public CategoriaTipoEvento(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
