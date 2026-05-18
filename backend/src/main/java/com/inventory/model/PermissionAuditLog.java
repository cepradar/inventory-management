package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro inmutable de cada cambio en los permisos de un rol.
 * Se inserta al otorgar/revocar un permiso; nunca se edita.
 */
@Entity
@Table(name = "permission_audit_log", indexes = {
    @Index(name = "idx_pal_role_name", columnList = "role_name"),
    @Index(name = "idx_pal_changed_at", columnList = "changed_at")
})
public class PermissionAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, length = 120)
    private String roleName;

    @Column(name = "permission_code", nullable = false, length = 120)
    private String permissionCode;

    /** Username del usuario que realizó el cambio */
    @Column(name = "changed_by", nullable = false, length = 120)
    private String changedBy;

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(name = "old_value")
    private Boolean oldValue;

    @Column(name = "new_value", nullable = false)
    private Boolean newValue;

    /** Razón opcional aportada por el administrador */
    @Column(length = 500)
    private String reason;

    public PermissionAuditLog() {}

    public PermissionAuditLog(String roleName, String permissionCode, String changedBy,
                               Boolean oldValue, Boolean newValue, String reason) {
        this.roleName = roleName;
        this.permissionCode = permissionCode;
        this.changedBy = changedBy;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.reason = reason;
        this.changedAt = LocalDateTime.now();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getRoleName() { return roleName; }
    public String getPermissionCode() { return permissionCode; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public Boolean getOldValue() { return oldValue; }
    public Boolean getNewValue() { return newValue; }
    public String getReason() { return reason; }
}
