package com.inventory.config;

import com.inventory.model.CategoriaTipoEvento;
import com.inventory.model.TipoEvento;
import com.inventory.repository.CategoriaTipoEventoRepository;
import com.inventory.repository.TipoEventoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inicializa los TipoEvento requeridos para el flujo de Órdenes de Servicio
 * si aún no existen en la base de datos.
 *
 * Complementa a TipoEventoInitializer garantizando que los eventos clave
 * de órdenes existan, sin depender de IDs de categoría hardcodeados.
 */
@Configuration
public class OrdenServicioDataInitializer {

    @Bean
    CommandLineRunner initTipoEventoAsignada(
            TipoEventoRepository tipoEventoRepository,
            CategoriaTipoEventoRepository categoriaTipoEventoRepository) {
        return args -> {
            if (tipoEventoRepository.existsById("SOA")) {
                return; // ya existe, nada que hacer
            }

            // Buscar la categoría ORDEN por nombre (robusto ante distintos IDs en BD)
            CategoriaTipoEvento categoria = categoriaTipoEventoRepository
                    .findByNombre("ORDEN")
                    .orElse(null);

            if (categoria == null) {
                // La categoría aún no existe; TipoEventoInitializer la creará en la misma
                // arranque. Si este bean se ejecuta antes, no podemos hacer nada aquí.
                return;
            }

            TipoEvento soaEvento = new TipoEvento("SOA", "ORDEN_SERVICIO_ASIGNADA", categoria);
            tipoEventoRepository.save(soaEvento);
        };
    }
}
