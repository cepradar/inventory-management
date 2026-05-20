package com.inventory.config;

import com.inventory.model.Permisos;
import com.inventory.model.RolePermission;
import com.inventory.model.Rol;
import com.inventory.repository.PermisosRepository;
import com.inventory.repository.RolePermissionRepository;
import com.inventory.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class PermisosInitializer {

    /**
     * Catálogo base de permisos granulares.
     * Cada entry: { code, label, moduleKey, categoryKey, actionKey }
     */
    private static final List<String[]> CATALOG = List.of(
        // users
        new String[]{"users.read",           "Ver usuarios",                "users",     "management", "read"},
        new String[]{"users.create",          "Crear usuarios",              "users",     "management", "create"},
        new String[]{"users.update",          "Editar usuarios",             "users",     "management", "update"},
        new String[]{"users.delete",          "Eliminar usuarios",           "users",     "management", "delete"},
        new String[]{"users.reset_password",  "Resetear contraseña",         "users",     "management", "reset_password"},
        new String[]{"users.assign_role",     "Asignar roles",               "users",     "management", "assign_role"},
        // inventory
        new String[]{"inventory.read",        "Ver inventario",              "inventory", null,         "read"},
        new String[]{"inventory.create",      "Agregar productos",           "inventory", null,         "create"},
        new String[]{"inventory.update",      "Editar productos",            "inventory", null,         "update"},
        new String[]{"inventory.delete",      "Eliminar productos",          "inventory", null,         "delete"},
        new String[]{"inventory.export",      "Exportar inventario",         "inventory", null,         "export"},
        // clients
        new String[]{"clients.read",          "Ver clientes",                "clients",   null,         "read"},
        new String[]{"clients.create",        "Crear clientes",              "clients",   null,         "create"},
        new String[]{"clients.update",        "Editar clientes",             "clients",   null,         "update"},
        new String[]{"clients.delete",        "Eliminar clientes",           "clients",   null,         "delete"},
        // sales
        new String[]{"sales.read",            "Ver ventas",                  "sales",     null,         "read"},
        new String[]{"sales.create",          "Registrar ventas",            "sales",     null,         "create"},
        new String[]{"sales.invoice.pdf",     "Generar factura PDF",         "sales",     "documents",  "invoice.pdf"},
        new String[]{"sales.delete",          "Anular ventas",               "sales",     null,         "delete"},
        // orders
        new String[]{"orders.read",           "Ver órdenes de servicio",     "orders",    null,         "read"},
        new String[]{"orders.create",         "Crear órdenes de servicio",   "orders",    null,         "create"},
        new String[]{"orders.update",         "Editar órdenes de servicio",  "orders",    null,         "update"},
        new String[]{"orders.delete",         "Eliminar órdenes",            "orders",    null,         "delete"},
        new String[]{"orders.assign_tech",    "Asignar técnico",             "orders",    null,         "assign_tech"},
        new String[]{"orders.pdf",            "Generar PDF de orden",        "orders",    "documents",  "pdf"},
        // audit
        new String[]{"audit.read",            "Ver auditoría",               "audit",     null,         "read"},
        new String[]{"audit.export",          "Exportar auditoría",          "audit",     null,         "export"},
        // config — tipos de usuarios (roles)
        new String[]{"config.user-types.read",   "Ver tipos de usuarios",               "config", "user-types",     "read"},
        new String[]{"config.user-types.create", "Crear tipos de usuarios",             "config", "user-types",     "create"},
        new String[]{"config.user-types.update", "Editar tipos de usuarios",            "config", "user-types",     "update"},
        new String[]{"config.user-types.delete", "Eliminar tipos de usuarios",          "config", "user-types",     "delete"},
        // config — tipos de documento
        new String[]{"config.document-types.read",   "Ver tipos de documento",          "config", "document-types", "read"},
        new String[]{"config.document-types.create", "Crear tipos de documento",        "config", "document-types", "create"},
        new String[]{"config.document-types.update", "Editar tipos de documento",       "config", "document-types", "update"},
        new String[]{"config.document-types.delete", "Eliminar tipos de documento",     "config", "document-types", "delete"},
        // config — categorías de electrodomésticos
        new String[]{"config.appliance-cat.read",   "Ver categorías de electrodomésticos",    "config", "appliance-cat", "read"},
        new String[]{"config.appliance-cat.create", "Crear categorías de electrodomésticos",  "config", "appliance-cat", "create"},
        new String[]{"config.appliance-cat.update", "Editar categorías de electrodomésticos", "config", "appliance-cat", "update"},
        new String[]{"config.appliance-cat.delete", "Eliminar categorías de electrodomésticos","config","appliance-cat", "delete"},
        // config — categorías de productos
        new String[]{"config.product-cat.read",   "Ver categorías de productos",         "config", "product-cat", "read"},
        new String[]{"config.product-cat.create", "Crear categorías de productos",       "config", "product-cat", "create"},
        new String[]{"config.product-cat.update", "Editar categorías de productos",      "config", "product-cat", "update"},
        new String[]{"config.product-cat.delete", "Eliminar categorías de productos",    "config", "product-cat", "delete"},
        // config — permisos por rol
        new String[]{"config.roles.read",  "Ver permisos por rol",       "config", "roles", "read"},
        new String[]{"config.roles.write", "Gestionar permisos por rol", "config", "roles", "write"},
        // config — empresa
        new String[]{"config.company.read",  "Ver configuración empresa",   "config", "company", "read"},
        new String[]{"config.company.write", "Editar configuración empresa","config", "company", "write"},
        // config — reportes (plantillas); códigos reports.* se mantienen para no romper guards existentes
        new String[]{"reports.read",     "Ver reportes",             "config", "reports", "read"},
        new String[]{"reports.download", "Descargar plantillas",     "config", "reports", "download"},
        new String[]{"reports.upload",   "Subir plantillas",         "config", "reports", "upload"},
        new String[]{"reports.update",   "Reemplazar plantillas",    "config", "reports", "update"},
        new String[]{"reports.toggle",   "Activar/Desactivar plantillas","config","reports", "toggle"},
        new String[]{"reports.delete",   "Eliminar plantillas",      "config", "reports", "delete"},
        new String[]{"reports.export",   "Exportar reportes",        "config", "reports", "export"},
        new String[]{"reports.preview",  "Vista previa de reportes", "config", "reports", "preview"},
        // services — dominio independiente de servicios técnicos
        new String[]{"services.read",   "Ver servicios",      "services", null, "read"},
        new String[]{"services.create", "Crear servicios",    "services", null, "create"},
        new String[]{"services.update", "Editar servicios",   "services", null, "update"},
        new String[]{"services.delete", "Eliminar servicios", "services", null, "delete"}
    );

    /** Permisos asignados por defecto a cada rol */
    private static final Map<String, List<String>> ROLE_DEFAULTS = Map.of(
        "ADMIN",   CATALOG.stream().map(e -> e[0]).toList(),
        "TECNICO", List.of("inventory.read", "clients.read", "clients.create", "clients.update",
                           "sales.read", "sales.create", "sales.invoice.pdf",
                           "orders.read", "orders.create", "orders.update", "orders.assign_tech",
                           "orders.pdf", "audit.read",
                           "reports.read", "reports.preview",
                           "services.read",
                           "config.user-types.read", "config.appliance-cat.read", "config.product-cat.read"),
        "CLIENTE", List.of("inventory.read", "orders.read", "sales.read")
    );

    @Bean
    CommandLineRunner initPermisos(PermisosRepository permisosRepo,
                                   RolePermissionRepository rolePermissionRepo,
                                   RolesRepository rolesRepo) {
        return args -> {
            // 1. Seed del catálogo de permisos — upsert: inserta si no existe, actualiza si cambió moduleKey/categoryKey
            for (String[] entry : CATALOG) {
                permisosRepo.findByCode(entry[0]).ifPresentOrElse(
                    existing -> {
                        boolean changed =
                            !Objects.equals(existing.getLabel(),       entry[1]) ||
                            !Objects.equals(existing.getModuleKey(),   entry[2]) ||
                            !Objects.equals(existing.getCategoryKey(), entry[3]) ||
                            !Objects.equals(existing.getActionKey(),   entry[4]);
                        if (changed) {
                            existing.setLabel(entry[1]);
                            existing.setModuleKey(entry[2]);
                            existing.setCategoryKey(entry[3]);
                            existing.setActionKey(entry[4]);
                            permisosRepo.save(existing);
                        }
                    },
                    () -> permisosRepo.save(
                        new Permisos(entry[0], entry[1], entry[2], entry[3], entry[4]))
                );
            }

            // 2. Sincronizar asignaciones por rol
            for (Map.Entry<String, List<String>> roleEntry : ROLE_DEFAULTS.entrySet()) {
                String roleName = roleEntry.getKey();
                List<String> activeCodes = roleEntry.getValue();
                Rol role = rolesRepo.findByName(roleName);
                if (role == null) continue;

                for (String[] entry : CATALOG) {
                    String code = entry[0];
                    Permisos permiso = permisosRepo.findByCode(code).orElse(null);
                    if (permiso == null) continue;

                    boolean shouldBeActive = activeCodes.contains(code);
                    rolePermissionRepo.findByRoleNameAndPermissionCode(roleName, code)
                        .ifPresentOrElse(
                            rp -> {
                                if (rp.isActive() != shouldBeActive) {
                                    rp.setActive(shouldBeActive);
                                    rolePermissionRepo.save(rp);
                                }
                            },
                            () -> rolePermissionRepo.save(
                                new RolePermission(role, permiso, shouldBeActive, "system"))
                        );
                }
            }
        };
    }
}

