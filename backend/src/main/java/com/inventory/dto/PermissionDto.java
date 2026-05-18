package com.inventory.dto;

import com.inventory.model.Permisos;

/**
 * Representa un permiso en el catálogo con todos los metadatos.
 */
public class PermissionDto {

    private Long id;
    private String code;
    private String label;
    private String moduleKey;
    private String categoryKey;
    private String actionKey;
    private Boolean uiVisible;
    private Boolean critical;
    private Boolean active;

    // Estado de asignación (opcional — solo cuando se consulta en el contexto de un rol)
    private Boolean assigned;
    private String grantedBy;

    public PermissionDto() {}

    public PermissionDto(Permisos p) {
        this.id = p.getId();
        this.code = p.getCode();
        this.label = p.getLabel();
        this.moduleKey = p.getModuleKey();
        this.categoryKey = p.getCategoryKey();
        this.actionKey = p.getActionKey();
        this.uiVisible = p.getUiVisible();
        this.critical = p.getCritical();
        this.active = p.getActive();
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getModuleKey() { return moduleKey; }
    public void setModuleKey(String moduleKey) { this.moduleKey = moduleKey; }

    public String getCategoryKey() { return categoryKey; }
    public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

    public String getActionKey() { return actionKey; }
    public void setActionKey(String actionKey) { this.actionKey = actionKey; }

    public Boolean getUiVisible() { return uiVisible; }
    public void setUiVisible(Boolean uiVisible) { this.uiVisible = uiVisible; }

    public Boolean getCritical() { return critical; }
    public void setCritical(Boolean critical) { this.critical = critical; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getAssigned() { return assigned; }
    public void setAssigned(Boolean assigned) { this.assigned = assigned; }

    public String getGrantedBy() { return grantedBy; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }
}
