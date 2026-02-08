package com.inventory.config;

import com.inventory.model.Rol;
import com.inventory.model.User;
import com.inventory.repository.RolesRepository;
import com.inventory.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

    private static final String ADMIN_USERNAME = "ADMIN";
    private static final String ADMIN_PASSWORD = "ADMIN";

    @Bean
    CommandLineRunner initAdminUser(UserRepository userRepository,
                                   RolesRepository rolesRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.existsById(ADMIN_USERNAME)) {
                return;
            }

            Rol adminRole = rolesRepository.findByName("ADMIN");
            if (adminRole == null) {
                adminRole = rolesRepository.save(new Rol("ADMIN", "#4f46e5", "Administrador del sistema"));
            }

            User admin = new User();
            admin.setUsername(ADMIN_USERNAME);
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole(adminRole);
            admin.setFirstName("Admin");
            admin.setLastName("Inicial");
            admin.setEmail("admin@local");

            userRepository.save(admin);
        };
    }
}
