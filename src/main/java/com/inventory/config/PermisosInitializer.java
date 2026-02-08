package com.inventory.config;

import com.inventory.model.Permisos;
import com.inventory.model.Permisos_Usuario;
import com.inventory.model.Rol;
import com.inventory.repository.PermisosRepository;
import com.inventory.repository.PermisosUsuarioRepository;
import com.inventory.repository.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class PermisosInitializer {

    private static final List<String> PERMISOS_BASE = Arrays.asList(
        "INVENTARIOS",
        "USUARIOS",
        "AUDITORIA",
        "VENTAS",
        "CONFIGURACION",
        "CLIENTES",
        "ORDENES_SERVICIO"
    );

    private static final List<String> PERMISOS_CLIENTE = Arrays.asList(
        "INVENTARIOS"
    );

    private static final List<String> PERMISOS_TECNICO = Arrays.asList(
        "AUDITORIA",
        "VENTAS",
        "CLIENTES",
        "ORDENES_SERVICIO"
    );

    @Bean
    CommandLineRunner initPermisos(PermisosRepository permisosRepository,
                                  PermisosUsuarioRepository permisosUsuarioRepository,
                                  RolesRepository rolesRepository) {
        return args -> {
            for (String permiso : PERMISOS_BASE) {
                permisosRepository.findByName(permiso)
                    .orElseGet(() -> permisosRepository.save(new Permisos(permiso)));
            }

            syncRolePermissions("ADMIN", PERMISOS_BASE, permisosRepository, permisosUsuarioRepository, rolesRepository);
            syncRolePermissions("CLIENTE", PERMISOS_CLIENTE, permisosRepository, permisosUsuarioRepository, rolesRepository);
            syncRolePermissions("TECNICO", PERMISOS_TECNICO, permisosRepository, permisosUsuarioRepository, rolesRepository);
        };
    }

    private void syncRolePermissions(String roleName,
                                     List<String> permisosActivos,
                                     PermisosRepository permisosRepository,
                                     PermisosUsuarioRepository permisosUsuarioRepository,
                                     RolesRepository rolesRepository) {
        Rol role = rolesRepository.findByName(roleName);
        if (role == null) {
            return;
        }

        for (String permiso : PERMISOS_BASE) {
            Permisos permEntity = permisosRepository.findByName(permiso).orElse(null);
            if (permEntity == null) {
                continue;
            }

            boolean shouldBeActive = permisosActivos.contains(permiso);
            Permisos_Usuario existing = permisosUsuarioRepository
                .findByRoleNameAndPermissionName(roleName, permiso)
                .orElse(null);

            if (existing == null) {
                permisosUsuarioRepository.save(new Permisos_Usuario(role, permEntity, shouldBeActive));
                continue;
            }

            if (existing.isActive() != shouldBeActive) {
                existing.setActive(shouldBeActive);
                permisosUsuarioRepository.save(existing);
            }
        }
    }
}
