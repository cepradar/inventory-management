package com.inventory.config;

import com.inventory.model.Servicio;
import com.inventory.repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@Order(6)
public class ServicioInitializer implements CommandLineRunner {

    @Autowired
    private ServicioRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            List<Servicio> servicios = Arrays.asList(
                new Servicio("Diagnóstico General", "Revisión completa del electrodoméstico para identificar fallas", new BigDecimal("25000")),
                new Servicio("Mantenimiento Preventivo", "Limpieza y ajustes para prevenir fallas", new BigDecimal("35000")),
                new Servicio("Reparación de Motor", "Cambio o reparación del motor principal", new BigDecimal("80000")),
                new Servicio("Cambio de Termostato", "Sustitución del termostato dañado", new BigDecimal("45000")),
                new Servicio("Reparación de Circuito Eléctrico", "Diagnóstico y reparación de fallas eléctricas", new BigDecimal("55000")),
                new Servicio("Cambio de Compresor", "Sustitución del compresor (neveras/aires)", new BigDecimal("120000")),
                new Servicio("Instalación", "Instalación completa del electrodoméstico", new BigDecimal("50000")),
                new Servicio("Recarga de Gas", "Recarga de gas refrigerante", new BigDecimal("60000")),
                new Servicio("Cambio de Tarjeta Electrónica", "Sustitución de placa madre/control", new BigDecimal("95000")),
                new Servicio("Limpieza Profunda", "Limpieza completa interna y externa", new BigDecimal("40000"))
            );
            repository.saveAll(servicios);
            System.out.println("✅ Catálogo de servicios inicializado con " + servicios.size() + " servicios");
        }
    }
}
