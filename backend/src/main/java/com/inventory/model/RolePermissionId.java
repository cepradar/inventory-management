package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RolePermissionId implements Serializable {

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

    public RolePermissionId() {}

    public RolePermissionId(String roleName, Long permissionId) {
        this.roleName = roleName;
        this.permissionId = permissionId;
    }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public Long getPermissionId() { return permissionId; }
    public void setPermissionId(Long permissionId) { this.permissionId = permissionId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermissionId that = (RolePermissionId) o;
        return Objects.equals(roleName, that.roleName) && Objects.equals(permissionId, that.permissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName, permissionId);
    }
}
