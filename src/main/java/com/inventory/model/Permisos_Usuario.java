package com.inventory.model;

import jakarta.persistence.*;

@Entity
public class Permisos_Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permisos permission;

    @Column(nullable = false)
    private boolean isActive;  // Nuevo campo para indicar si el permiso está activo o inactivo

    // Constructor vacío requerido por JPA
    public Permisos_Usuario() {}

    public Permisos_Usuario(User user, Permisos permission) {
        this.user = user;
        this.permission = permission;
        this.isActive = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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
