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
                categoryProductRepository.save(new CategoryProduct("N","REPUESTOS NUEVOS","NUEVOS"));
            }
            if (!categoryProductRepository.findById("U").isPresent()) {
                categoryProductRepository.save(new CategoryProduct("U","REPUESTOS USADOS","USADOS"));
            }
            if (!categoryProductRepository.findById("S").isPresent()) {
                categoryProductRepository.save(new CategoryProduct("S","SERVICIOS","SERVICIOS"));
            }
        };
    }
}
