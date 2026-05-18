package com.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "permisos",
    uniqueConstraints = @UniqueConstraint(columnNames = "code"))
public class Permisos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código estable para autorizar, ej: "users.create", "sales.invoice.pdf" */
    @Column(nullable = false, unique = true, length = 120)
    private String code;

    /** Etiqueta legible para la UI, ej: "Crear usuarios" */
    @Column(nullable = false, length = 180)
    private String label;

    /** Módulo padre: users, inventory, clients, sales, orders, audit, config */
    @Column(name = "module_key", nullable = false, length = 80)
    private String moduleKey;

    /** Subcategoría opcional dentro del módulo */
    @Column(name = "category_key", length = 80)
    private String categoryKey;

    /** Acción base: read, create, update, delete, export, etc. */
    @Column(name = "action_key", nullable = false, length = 80)
    private String actionKey;

    /** Mostrar en el panel de permisos de la UI */
    @Column(name = "ui_visible", nullable = false)
    private Boolean uiVisible = true;

    /** Permiso sensible (requiere confirmación extra en UI) */
    @Column(nullable = false)
    private Boolean critical = false;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Permisos() {}

    public Permisos(String code, String label, String moduleKey,
                    String categoryKey, String actionKey) {
        this.code = code;
        this.label = label;
        this.moduleKey = moduleKey;
        this.categoryKey = categoryKey;
        this.actionKey = actionKey;
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    /** Alias legacy — devuelve code para no romper código existente */
    public String getName() { return code; }
    public void setName(String name) { this.code = name; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
}

