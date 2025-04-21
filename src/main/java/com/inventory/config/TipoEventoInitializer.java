package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.inventory.model.TipoEvento;
import com.inventory.repository.TipoEventoRepository;

@Configuration
public class TipoEventoInitializer {

    @Bean
    CommandLineRunner initTipoEventos(TipoEventoRepository eventoRepository) {
        return args -> {
            if (eventoRepository.findByNombre("COMPRA") == null) {
                eventoRepository.save(new TipoEvento("COMPRA"));
            }
            if (eventoRepository.findByNombre("VENTA") == null) {
                eventoRepository.save(new TipoEvento( "VENTA"));
            }
        };
    }
}
