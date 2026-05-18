package com.inventory.config;

import com.inventory.model.ReportTemplate;
import com.inventory.repository.ReportTemplateRepository;
import com.inventory.service.ReportStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Inicializa las plantillas del sistema (factura y orden de servicio) en la base de datos
 * al arrancar la aplicación. Solo crea los registros si no existen previamente.
 */
@Configuration
public class ReportesInicializador {

    private static final Logger log = LoggerFactory.getLogger(ReportesInicializador.class);

    @Bean
    public CommandLineRunner initReportTemplates(
            ReportTemplateRepository repository,
            ReportStorageService storageService) {
        return args -> {
            initSystemTemplate(
                    repository, storageService,
                    "Plantilla Sistema - Factura",
                    ReportTemplate.TipoReporte.FACTURA,
                    "reports/factura.jrxml",
                    "system_factura.jrxml",
                    "Plantilla oficial del sistema para la generación de facturas de venta"
            );
            initSystemTemplate(
                    repository, storageService,
                    "Plantilla Sistema - Orden de Servicio",
                    ReportTemplate.TipoReporte.ORDEN_SERVICIO,
                    "reports/orden_servicio.jrxml",
                    "system_orden_servicio.jrxml",
                    "Plantilla oficial del sistema para la generación de órdenes de servicio"
            );
        };
    }

    private void initSystemTemplate(
            ReportTemplateRepository repository,
            ReportStorageService storageService,
            String nombre,
            ReportTemplate.TipoReporte tipo,
            String classpathResource,
            String storageFilename,
            String descripcion) {

        // Si ya existe en BD, solo asegurarnos de que el archivo también existe en storage
        repository.findByNombre(nombre).ifPresentOrElse(
                existing -> {
                    try {
                        if (!storageService.fileExists(storageFilename)) {
                            byte[] content = loadFromClasspath(classpathResource);
                            if (content != null) {
                                storageService.saveFile(content, storageFilename);
                                log.info("Archivo de plantilla sistema recuperado: {}", storageFilename);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("No se pudo verificar/recuperar el archivo {}: {}", storageFilename, e.getMessage());
                    }
                },
                () -> {
                    // Crear registro nuevo
                    byte[] content = loadFromClasspath(classpathResource);
                    if (content == null) {
                        log.warn("No se encontró plantilla del sistema en classpath: {}", classpathResource);
                        return;
                    }
                    try {
                        storageService.saveFile(content, storageFilename);

                        ReportTemplate template = new ReportTemplate();
                        template.setNombre(nombre);
                        template.setTipoReporte(tipo);
                        template.setDescripcion(descripcion);
                        template.setArchivoJrxmlNombre(storageFilename);
                        template.setActivo(true);
                        template.setEsSistema(true);
                        template.setCreadoPor("sistema");
                        template.setFechaActualizacion(LocalDateTime.now());

                        repository.save(template);
                        log.info("Plantilla del sistema inicializada en BD: {}", nombre);
                    } catch (Exception e) {
                        log.error("Error al inicializar plantilla del sistema '{}': {}", nombre, e.getMessage());
                    }
                }
        );
    }

    private byte[] loadFromClasspath(String resource) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (is == null) return null;
            return is.readAllBytes();
        } catch (Exception e) {
            log.error("Error leyendo classpath resource {}: {}", resource, e.getMessage());
            return null;
        }
    }
}
