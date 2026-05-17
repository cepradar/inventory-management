package com.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "permisos_usuario")
public class Permisos_Usuario {

    @EmbeddedId
    private PermisosUsuarioId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleName")
    @JoinColumn(name = "role_name", nullable = false)
    private Rol role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    private Permisos permission;

    @Column(nullable = false)
    private boolean isActive;  // Nuevo campo para indicar si el permiso está activo o inactivo

    // Constructor vacío requerido por JPA
    public Permisos_Usuario() {}

    public Permisos_Usuario(Rol role, Permisos permission, boolean isActive) {
        this.role = role;
        this.permission = permission;
        this.isActive = isActive;
        this.id = new PermisosUsuarioId(
            role != null ? role.getName() : null,
            permission != null ? permission.getId() : null
        );
    }

    // Getters y Setters
    public PermisosUsuarioId getId() {
        return id;
    }

    public void setId(PermisosUsuarioId id) {
        this.id = id;
    }

    public Rol getRole() {
        return role;
    }

    public void setRole(Rol role) {
        this.role = role;
        if (this.id == null) {
            this.id = new PermisosUsuarioId();
        }
        this.id.setRoleName(role != null ? role.getName() : null);
    }

    public Permisos getPermission() {
        return permission;
    }

    public void setPermission(Permisos permission) {
        this.permission = permission;
        if (this.id == null) {
            this.id = new PermisosUsuarioId();
        }
        this.id.setPermissionId(permission != null ? permission.getId() : null);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
