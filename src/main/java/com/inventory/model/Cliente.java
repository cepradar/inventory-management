package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @Column(nullable = false, unique = true)
    private String id;

    private String nit;

    @JsonBackReference // Evita la serialización recursiva
    @ManyToOne(fetch = FetchType.EAGER)  // Puedes cambiarlo a LAZY si es necesario
    @JoinColumn(name = "tipo_cliente", nullable = false)
    private CategoryClient category;

    @JsonBackReference // Evita la serialización recursiva
    @ManyToOne(fetch = FetchType.EAGER)  // Puedes cambiarlo a LAZY si es necesario
    @JoinColumn(name = "tipo_documento", nullable = false)
    private DocumentoTipo tipoDocumento;

    private String nombre;

    private String telefono;
    private String direccion;
    private Boolean activo;

    public Cliente() {
    }

    public Cliente(String id, String nit, CategoryClient category, DocumentoTipo tipoDocumento, String nombre, String telefono,
            String direccion, Boolean activo) {
        this.id = id;
        this.nit = nit;
        this.category = category;
        this.tipoDocumento = tipoDocumento;
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public CategoryClient getCategory() {
        return category;
    }

    public void setCategory(CategoryClient category) {
        this.category = category;
    }

    public DocumentoTipo getTipoId() {
        return tipoDocumento;
    }

    public void setTipoId(DocumentoTipo tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

}
