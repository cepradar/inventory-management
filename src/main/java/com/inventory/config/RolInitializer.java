package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inventory.model.Rol;
import com.inventory.repository.RolesRepository;

@Configuration
public class RolInitializer {

    @Bean
    CommandLineRunner initRoles(RolesRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ADMIN") == null) {
                Rol adminRole = new Rol("ADMIN", "#4f46e5");
                roleRepository.save(adminRole);
            }
            if (roleRepository.findByName("CLIENTE") == null) {
                Rol clienteRole = new Rol("CLIENTE", "#2563eb");
                roleRepository.save(clienteRole);
            }
            if (roleRepository.findByName("TECNICO") == null) {
                Rol tecnicoRole = new Rol("TECNICO", "#16a34a");
                roleRepository.save(tecnicoRole);
            }
            if (roleRepository.findByName("USER") == null) {
                Rol userRole = new Rol("USER", "#2563eb");
                roleRepository.save(userRole);
            }
        };
    }
}
