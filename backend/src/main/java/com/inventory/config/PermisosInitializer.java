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
        // config
        new String[]{"config.roles.read",     "Ver roles y permisos",        "config",    "roles",      "read"},
        new String[]{"config.roles.write",    "Gestionar roles y permisos",  "config",    "roles",      "write"},
        new String[]{"config.company.read",   "Ver configuración empresa",   "config",    "company",    "read"},
        new String[]{"config.company.write",  "Editar configuración empresa","config",    "company",    "write"},
        // reports
        new String[]{"reports.read",          "Ver reportes",                "reports",   null,         "read"},
        new String[]{"reports.export",        "Exportar reportes",           "reports",   null,         "export"}
    );

    /** Permisos asignados por defecto a cada rol */
    private static final Map<String, List<String>> ROLE_DEFAULTS = Map.of(
        "ADMIN",   CATALOG.stream().map(e -> e[0]).toList(),
        "TECNICO", List.of("inventory.read", "clients.read", "clients.create", "clients.update",
                           "sales.read", "sales.create", "sales.invoice.pdf",
                           "orders.read", "orders.create", "orders.update", "orders.assign_tech",
                           "orders.pdf", "audit.read"),
        "CLIENTE", List.of("inventory.read", "orders.read", "sales.read")
    );

    @Bean
    CommandLineRunner initPermisos(PermisosRepository permisosRepo,
                                   RolePermissionRepository rolePermissionRepo,
                                   RolesRepository rolesRepo) {
        return args -> {
            // 1. Seed del catálogo de permisos (idempotente)
            for (String[] entry : CATALOG) {
                permisosRepo.findByCode(entry[0]).orElseGet(() -> {
                    Permisos p = new Permisos(entry[0], entry[1], entry[2], entry[3], entry[4]);
                    return permisosRepo.save(p);
                });
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

