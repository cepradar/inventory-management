package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = true)
    private String color;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Se incrementa cada vez que cambian los permisos de este rol.
     * Permite invalidar caché o comparar con la versión guardada en el token.
     */
    @Column(name = "permissions_version", nullable = false)
    private Long permissionsVersion = 1L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Rol() {}

    public Rol(String name) { this.name = name; }

    public Rol(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Rol(String name, String color, String description) {
        this.name = name;
        this.color = color;
        this.description = description;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Long getPermissionsVersion() { return permissionsVersion; }
    public void setPermissionsVersion(Long permissionsVersion) { this.permissionsVersion = permissionsVersion; }

    public void incrementPermissionsVersion() {
        this.permissionsVersion = (this.permissionsVersion == null ? 1L : this.permissionsVersion) + 1;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Rol[name=" + name + ", active=" + active + ", v=" + permissionsVersion + "]";
    }
}

