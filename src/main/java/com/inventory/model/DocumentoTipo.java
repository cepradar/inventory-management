package com.inventory.model;

import jakarta.persistence.*;

@Entity
public class DocumentoTipo {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String name;
    private Boolean activo;

    public DocumentoTipo() {}

    public DocumentoTipo(String id, String name, Boolean activo) {
        this.id = id;
        this.name = name;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
