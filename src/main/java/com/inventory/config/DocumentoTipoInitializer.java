package com.inventory.config;

import com.inventory.model.DocumentoTipo;
import com.inventory.repository.DocumentoTipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DocumentoTipoInitializer implements CommandLineRunner {

    @Autowired
    private DocumentoTipoRepository documentoTipoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (documentoTipoRepository.count() == 0) {
            // Crear tipos de documento por defecto
            DocumentoTipo cc = new DocumentoTipo("CC", "Cédula de Ciudadanía", true);
            DocumentoTipo nit = new DocumentoTipo("NIT", "Número de Identificación Tributaria", true);
            DocumentoTipo ce = new DocumentoTipo("CE", "Cédula de Extranjería", true);
            DocumentoTipo pasaporte = new DocumentoTipo("PASAPORTE", "Pasaporte", true);
            DocumentoTipo ti = new DocumentoTipo("TI", "Tarjeta de Identidad", true);

            documentoTipoRepository.save(cc);
            documentoTipoRepository.save(nit);
            documentoTipoRepository.save(ce);
            documentoTipoRepository.save(pasaporte);
            documentoTipoRepository.save(ti);

            System.out.println("Tipos de documento inicializados correctamente");
        }
    }
}
