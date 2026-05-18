package com.inventory.dto;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Árbol de catálogo agrupado por módulo → categoría → permisos.
 * Se usa en el panel de administración para renderizar dinámicamente
 * los checkboxes de permisos por rol.
 */
public class PermissionCatalogDto {

    /** Clave del módulo, ej: "users", "inventory", "sales" */
    private String moduleKey;

    /** Etiqueta legible del módulo para mostrar en UI */
    private String moduleLabel;

    /** Grupos de permisos dentro del módulo (por categoryKey) */
    private List<CategoryGroup> categories = new ArrayList<>();

    public PermissionCatalogDto() {}

    public PermissionCatalogDto(String moduleKey, String moduleLabel) {
        this.moduleKey = moduleKey;
        this.moduleLabel = moduleLabel;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getModuleKey() { return moduleKey; }
    public void setModuleKey(String moduleKey) { this.moduleKey = moduleKey; }

    public String getModuleLabel() { return moduleLabel; }
    public void setModuleLabel(String moduleLabel) { this.moduleLabel = moduleLabel; }

    public List<CategoryGroup> getCategories() { return categories; }
    public void setCategories(List<CategoryGroup> categories) { this.categories = categories; }

    // ── Clases internas ──────────────────────────────────────────────────────

    public static class CategoryGroup {
        private String categoryKey;
        private List<PermissionDto> permissions = new ArrayList<>();

        public CategoryGroup() {}

        public CategoryGroup(String categoryKey) {
            this.categoryKey = categoryKey;
        }

        public String getCategoryKey() { return categoryKey; }
        public void setCategoryKey(String categoryKey) { this.categoryKey = categoryKey; }

        public List<PermissionDto> getPermissions() { return permissions; }
        public void setPermissions(List<PermissionDto> permissions) { this.permissions = permissions; }
    }
}
