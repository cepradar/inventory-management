package com.inventory.model;

import jakarta.persistence.*;

@Entity
public class PermisosDeUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuarios user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permisos permission;

    @Column(nullable = false)
    private boolean isActive;  // Nuevo campo para indicar si el permiso está activo o inactivo

    // Constructor vacío requerido por JPA
    public PermisosDeUsuario() {}

    public PermisosDeUsuario(Usuarios user, Permisos permission) {
        this.user = user;
        this.permission = permission;
        this.isActive = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public Usuarios getUser() {
        return user;
    }

    public void setUser(Usuarios user) {
        this.user = user;
    }

    public Permisos getPermission() {
        return permission;
    }

    public void setPermission(Permisos permission) {
        this.permission = permission;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
