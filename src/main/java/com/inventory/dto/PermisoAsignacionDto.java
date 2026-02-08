package com.inventory.dto;

public class PermisoAsignacionDto {
    private Long permissionId;
    private String permissionName;
    private boolean active;

    public PermisoAsignacionDto() {}

    public PermisoAsignacionDto(Long permissionId, String permissionName, boolean active) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
        this.active = active;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
