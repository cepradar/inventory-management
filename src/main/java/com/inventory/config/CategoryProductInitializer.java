package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inventory.model.CategoryProduct;
import com.inventory.repository.CategoryProductRepository;

@Configuration
public class CategoryProductInitializer {

    @Bean
    CommandLineRunner initCategoryProduct(CategoryProductRepository categoryProductRepository) {
        System.out.println("se creo productos");
        return args -> {
            if (!categoryProductRepository.findById("N").isPresent()) {
                System.out.println("se creo producto nuevo");
                categoryProductRepository.save(new CategoryProduct("NUEVOS","REPUESTOS NUEVOS","N"));
            }
            if (!categoryProductRepository.findById("U").isPresent()) {
                categoryProductRepository.save(new CategoryProduct("USADOS","REPUESTOS USADOS","U"));
            }
           /*  
            if (categoryClientRepository.findByName("TECNICO") == null) {
                categoryClientRepository.save(new Rol( "TECNICO"));
            }*/
        };
    }
}
