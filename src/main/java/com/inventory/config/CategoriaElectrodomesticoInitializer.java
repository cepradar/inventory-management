package com.inventory.config;

import com.inventory.model.CategoriaElectrodomestico;
import com.inventory.repository.CategoriaElectrodomesticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Order(5)
public class CategoriaElectrodomesticoInitializer implements CommandLineRunner {

    @Autowired
    private CategoriaElectrodomesticoRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            List<CategoriaElectrodomestico> categorias = Arrays.asList(
                new CategoriaElectrodomestico("Nevera", "Refrigeradores y congeladores"),
                new CategoriaElectrodomestico("Lavadora", "Lavadoras de ropa"),
                new CategoriaElectrodomestico("Secadora", "Secadoras de ropa"),
                new CategoriaElectrodomestico("Microondas", "Hornos microondas"),
                new CategoriaElectrodomestico("Horno", "Hornos eléctricos y a gas"),
                new CategoriaElectrodomestico("Estufa", "Estufas y cocinas"),
                new CategoriaElectrodomestico("Televisor", "Televisores y pantallas"),
                new CategoriaElectrodomestico("Aire Acondicionado", "Sistemas de climatización"),
                new CategoriaElectrodomestico("Ventilador", "Ventiladores de techo y piso"),
                new CategoriaElectrodomestico("Licuadora", "Licuadoras y procesadores"),
                new CategoriaElectrodomestico("Cafetera", "Cafeteras eléctricas"),
                new CategoriaElectrodomestico("Plancha", "Planchas de vapor"),
                new CategoriaElectrodomestico("Aspiradora", "Aspiradoras"),
                new CategoriaElectrodomestico("Freidora", "Freidoras de aire y eléctricas"),
                new CategoriaElectrodomestico("Tostadora", "Tostadoras de pan"),
                new CategoriaElectrodomestico("Batidora", "Batidoras eléctricas"),
                new CategoriaElectrodomestico("Otro", "Otros electrodomésticos")
            );
            repository.saveAll(categorias);
            System.out.println("✅ Categorías de electrodomésticos inicializadas");
        }
    }
}
