package com.inventory.config;

import com.inventory.model.MarcaElectrodomestico;
import com.inventory.repository.MarcaElectrodomesticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MarcaElectrodomesticoInitializer implements CommandLineRunner {

    @Autowired
    private MarcaElectrodomesticoRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            List<String> marcas = Arrays.asList(
                "Samsung", "LG", "Whirlpool", "Mabe", "General Electric",
                "Electrolux", "Bosch", "Panasonic", "Sony", "Philips",
                "Haceb", "Centrales", "Challenger", "Kalley", "Imusa",
                "Oster", "Black+Decker", "Cuisinart", "KitchenAid", "Frigidaire"
            );
            
            for (String nombre : marcas) {
                MarcaElectrodomestico marca = new MarcaElectrodomestico(nombre);
                repository.save(marca);
            }
            
            System.out.println("Marcas de electrodomésticos inicializadas");
        }
    }
}
