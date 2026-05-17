package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inventory.model.CategoryClient;
import com.inventory.repository.CategoryClientRepository;

@Configuration
public class CategoryClientInitializer {

    @Bean
    CommandLineRunner initCategoryClient(CategoryClientRepository categoryClientRepository) {
        System.out.println("se creo clientess");
        return args -> {
            if (!categoryClientRepository.findById("PART").isPresent()) {
                categoryClientRepository.save(new CategoryClient("PART","PARTICULARES",true));
            }
            if (!categoryClientRepository.findById("E").isPresent()) {
                categoryClientRepository.save(new CategoryClient("E","EMPRESAS",true));
            }
           /*  if (categoryClientRepository.findByName("CLIENTE") == null) {
                categoryClientRepository.save(new Rol( "CLIENTE"));
            }
            if (categoryClientRepository.findByName("TECNICO") == null) {
                categoryClientRepository.save(new Rol( "TECNICO")); 
            }*/
        };
    }
}
