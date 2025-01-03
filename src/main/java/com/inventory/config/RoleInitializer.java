package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inventory.model.Roles;
import com.inventory.repository.RolesRepository;

@Configuration
public class RoleInitializer {

    @Bean
    CommandLineRunner initRoles(RolesRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ADMIN") == null) {
                roleRepository.save(new Roles( "ADMIN"));
            }
            if (roleRepository.findByName("CLIENTE") == null) {
                roleRepository.save(new Roles( "CLIENTE"));
            }
            if (roleRepository.findByName("TECNICO") == null) {
                roleRepository.save(new Roles( "TECNICO"));
            }
        };
    }
}
