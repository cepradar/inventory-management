package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inventory.model.Role;
import com.inventory.repository.RoleRepository;

@Configuration
public class RoleInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ADMIN") == null) {
                roleRepository.save(new Role(null, "ADMIN"));
            }
            if (roleRepository.findByName("CLIENTE") == null) {
                roleRepository.save(new Role(null, "CLIENTE"));
            }
            if (roleRepository.findByName("TECNICO") == null) {
                roleRepository.save(new Role(null, "TECNICO"));
            }
        };
    }
}
