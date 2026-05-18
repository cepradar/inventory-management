package com.inventory.controller;

import com.inventory.dto.ReportTemplateDto;
import com.inventory.service.ReportTemplateService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador REST para la gestión de plantillas de reportes.
 * Todas las rutas están bajo /api/reportes.
 */
@RestController
@RequestMapping("/api/reportes")
public class ReportTemplateController {

    private final ReportTemplateService reportTemplateService;

    public ReportTemplateController(ReportTemplateService reportTemplateService) {
        this.reportTemplateService = reportTemplateService;
    }

    /**
     * Lista todas las plantillas de reportes.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<List<ReportTemplateDto>> listar() {
        return ResponseEntity.ok(reportTemplateService.listarTodos());
    }

    /**
     * Obtiene una plantilla por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<ReportTemplateDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(reportTemplateService.obtenerPorId(id));
    }

    /**
     * Sube una plantilla nueva (.jrxml o .jasper).
     * Soporta los campos nuevos de módulo y tipoDocumento.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportTemplateDto> subir(
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "tipoReporte", required = false) String tipoReporte,
            @RequestParam(value = "modulo", required = false) String modulo,
            @RequestParam(value = "tipoDocumento", required = false) String tipoDocumento,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam("archivo") MultipartFile archivo,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            ReportTemplateDto result = reportTemplateService.subirPlantilla(
                    nombre, tipoReporte, modulo, tipoDocumento, descripcion, archivo, username);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Descarga el archivo .jrxml de una plantilla.
     */
    @GetMapping("/{id}/download/jrxml")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarJrxml(@PathVariable Long id) {
        try {
            byte[] bytes = reportTemplateService.descargarJrxml(id);
            ReportTemplateDto dto = reportTemplateService.obtenerPorId(id);
            String filename = (dto.getArchivoJrxmlNombre() != null ? dto.getArchivoJrxmlNombre() : "plantilla.jrxml");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Descarga el archivo .jasper precompilado de una plantilla.
     */
    @GetMapping("/{id}/download/jasper")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> descargarJasper(@PathVariable Long id) {
        try {
            byte[] bytes = reportTemplateService.descargarJasper(id);
            ReportTemplateDto dto = reportTemplateService.obtenerPorId(id);
            String filename = (dto.getArchivoJasperNombre() != null ? dto.getArchivoJasperNombre() : "plantilla.jasper");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Genera una vista previa PDF con datos de muestra.
     */
    @GetMapping("/{id}/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<byte[]> preview(@PathVariable Long id) {
        try {
            byte[] pdfBytes = reportTemplateService.previewPlantilla(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename("preview.pdf").build());
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualiza metadata de una plantilla (nombre, descripción, tipo).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportTemplateDto> actualizar(@PathVariable Long id,
                                                         @RequestBody ReportTemplateDto dto) {
        try {
            return ResponseEntity.ok(reportTemplateService.actualizarMetadata(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Activa o desactiva una plantilla.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportTemplateDto> cambiarEstado(@PathVariable Long id,
                                                            @RequestParam boolean activo) {
        try {
            return ResponseEntity.ok(reportTemplateService.cambiarEstado(id, activo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina una plantilla (solo no-sistema).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            reportTemplateService.eliminar(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
