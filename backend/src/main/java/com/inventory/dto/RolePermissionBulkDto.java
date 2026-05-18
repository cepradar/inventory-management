package com.inventory.dto;

/**
 * Elemento de la lista de asignación masiva de permisos a un rol.
 * El cliente envía la lista completa (todos los permisos + estado) para un rol.
 */
public class RolePermissionBulkDto {

    /** Código único del permiso, ej: "users.create" */
    private String permissionCode;

    /** true = otorgado, false = revocado */
    private boolean active;

    /** Razón opcional del cambio */
    private String reason;

    public RolePermissionBulkDto() {}

    public String getPermissionCode() { return permissionCode; }
    public void setPermissionCode(String permissionCode) { this.permissionCode = permissionCode; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
