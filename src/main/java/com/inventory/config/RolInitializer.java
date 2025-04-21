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
                roleRepository.save(new Rol( "ADMIN"));
            }
            if (roleRepository.findByName("CLIENTE") == null) {
                roleRepository.save(new Rol( "CLIENTE"));
            }
            if (roleRepository.findByName("TECNICO") == null) {
                roleRepository.save(new Rol( "TECNICO"));
            }
        };
    }
}
