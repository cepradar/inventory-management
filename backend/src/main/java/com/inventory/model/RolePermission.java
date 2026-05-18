package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleName")
    @JoinColumn(name = "role_name", nullable = false)
    private Rol role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id", nullable = false)
    private Permisos permission;

    /** true = permiso otorgado, false = permiso denegado explícitamente */
    @Column(nullable = false)
    private boolean isActive;

    /** Usuario que realizó la última modificación */
    @Column(name = "granted_by", length = 120)
    private String grantedBy;

    /** Fecha y hora de la última modificación */
    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    public RolePermission() {}

    public RolePermission(Rol role, Permisos permission, boolean isActive, String grantedBy) {
        this.role = role;
        this.permission = permission;
        this.isActive = isActive;
        this.grantedBy = grantedBy;
        this.grantedAt = LocalDateTime.now();
        this.id = new RolePermissionId(
            role != null ? role.getName() : null,
            permission != null ? permission.getId() : null
        );
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public RolePermissionId getId() { return id; }
    public void setId(RolePermissionId id) { this.id = id; }

    public Rol getRole() { return role; }
    public void setRole(Rol role) {
        this.role = role;
        if (this.id == null) this.id = new RolePermissionId();
        this.id.setRoleName(role != null ? role.getName() : null);
    }

    public Permisos getPermission() { return permission; }
    public void setPermission(Permisos permission) {
        this.permission = permission;
        if (this.id == null) this.id = new RolePermissionId();
        this.id.setPermissionId(permission != null ? permission.getId() : null);
    }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getGrantedBy() { return grantedBy; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }

    public LocalDateTime getGrantedAt() { return grantedAt; }
    public void setGrantedAt(LocalDateTime grantedAt) { this.grantedAt = grantedAt; }
}
