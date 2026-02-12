package com.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "company")
@Data
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String razonSocial;

    @Column(nullable = false, unique = true)
    private String nit;

    @Column(nullable = true)
    private String direccion;

    @Column(nullable = true)
    private String ciudad;

    @Column(nullable = true)
    private String departamento;

    @Column(nullable = true)
    private String codigoPostal;

    @Column(nullable = true)
    private String telefono;

    @Column(nullable = true)
    private String correo;

    @Column(nullable = true)
    private String sitioWeb;

    @Column(nullable = true)
    private String representanteLegal;

    @Column(nullable = true)
    private String numeroRegimen;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = true)
    private byte[] logo;

    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = true)
    private byte[] logo2;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Constructor
    public Company() {
    }

    public Company(String razonSocial, String nit) {
        this.razonSocial = razonSocial;
        this.nit = nit;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getRepresentanteLegal() {
        return representanteLegal;
    }

    public void setRepresentanteLegal(String representanteLegal) {
        this.representanteLegal = representanteLegal;
    }

    public String getNumeroRegimen() {
        return numeroRegimen;
    }

    public void setNumeroRegimen(String numeroRegimen) {
        this.numeroRegimen = numeroRegimen;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public byte[] getLogo2() {
        return logo2;
    }

    public void setLogo2(byte[] logo2) {
        this.logo2 = logo2;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", razonSocial='" + razonSocial + '\'' +
                ", nit='" + nit + '\'' +
                ", correo='" + correo + '\'' +
                '}';
    }
}
