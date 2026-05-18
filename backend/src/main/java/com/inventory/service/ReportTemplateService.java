package com.inventory.service;

import com.inventory.dto.ReportTemplateDto;
import com.inventory.model.ReportTemplate;
import com.inventory.repository.ReportTemplateRepository;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio CRUD para la gestión de plantillas de reportes.
 * Coordina entre la base de datos, el almacenamiento de archivos y la compilación JasperReports.
 */
@Service
public class ReportTemplateService {

    private static final Logger log = LoggerFactory.getLogger(ReportTemplateService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReportTemplateRepository repository;
    private final ReportStorageService storageService;
    private final JasperReportService jasperService;

    public ReportTemplateService(ReportTemplateRepository repository,
                                  ReportStorageService storageService,
                                  JasperReportService jasperService) {
        this.repository = repository;
        this.storageService = storageService;
        this.jasperService = jasperService;
    }

    // ── Consulta ──────────────────────────────────────────────────────────────

    public List<ReportTemplateDto> listarTodos() {
        return repository.findAllByOrderByFechaCreacionDesc()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    public ReportTemplateDto obtenerPorId(Long id) {
        ReportTemplate template = findOrThrow(id);
        return toDto(template);
    }

    // ── Creación / Subida ─────────────────────────────────────────────────────

    /**
     * Sube una plantilla nueva (JRXML o JASPER).
     *
     * @param nombre        nombre descriptivo
     * @param tipo          tipo de reporte legacy (ej: FACTURA) — puede ser null si se usa tipoDocumento
     * @param modulo        módulo funcional (ej: VENTAS) — puede ser null
     * @param tipoDocumento tipo de documento (ej: FACTURA_VENTA) — puede ser null
     * @param descripcion   descripción opcional
     * @param archivo       archivo .jrxml o .jasper
     * @param username      usuario que realiza la operación
     */
    @Transactional
    public ReportTemplateDto subirPlantilla(String nombre, String tipo,
                                             String modulo, String tipoDocumento,
                                             String descripcion, MultipartFile archivo,
                                             String username) throws Exception {

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debe proporcionar un archivo .jrxml o .jasper");
        }

        String originalFilename = archivo.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".jrxml") && !originalFilename.endsWith(".jasper"))) {
            throw new IllegalArgumentException("El archivo debe tener extensión .jrxml o .jasper");
        }

        ReportTemplate.TipoReporte tipoReporte = (tipo != null && !tipo.isBlank()) ? parseTipo(tipo) : null;

        ReportTemplate template = new ReportTemplate();
        template.setNombre(nombre);
        template.setTipoReporte(tipoReporte);
        if (modulo != null && !modulo.isBlank()) {
            try { template.setModulo(ReportTemplate.ModuloReporte.valueOf(modulo.toUpperCase())); } catch (Exception ignored) {}
        }
        if (tipoDocumento != null && !tipoDocumento.isBlank()) {
            try { template.setTipoDocumento(ReportTemplate.TipoDocumento.valueOf(tipoDocumento.toUpperCase())); } catch (Exception ignored) {}
        }
        template.setDescripcion(descripcion);
        template.setCreadoPor(username);
        template.setActivo(true);
        template.setEsSistema(false);
        template.setFechaActualizacion(LocalDateTime.now());

        byte[] content = archivo.getBytes();

        if (originalFilename.endsWith(".jrxml")) {
            // Compilar para validar el JRXML
            jasperService.compileJrxml(content);

            String storedName = UUID.randomUUID() + "_" + sanitizeFilename(originalFilename);
            storageService.saveFile(content, storedName);
            template.setArchivoJrxmlNombre(storedName);
        } else {
            // Archivo .jasper precompilado — guardar directamente
            String storedName = UUID.randomUUID() + "_" + sanitizeFilename(originalFilename);
            storageService.saveFile(content, storedName);
            template.setArchivoJasperNombre(storedName);
        }

        template = repository.save(template);
        log.info("Plantilla de reporte subida: {} por {}", nombre, username);
        return toDto(template);
    }

    // ── Descarga ──────────────────────────────────────────────────────────────

    public byte[] descargarJrxml(Long id) throws Exception {
        ReportTemplate template = findOrThrow(id);
        if (template.getArchivoJrxmlNombre() == null) {
            throw new IllegalStateException("Esta plantilla no tiene archivo JRXML asociado");
        }
        return storageService.readFile(template.getArchivoJrxmlNombre());
    }

    public byte[] descargarJasper(Long id) throws Exception {
        ReportTemplate template = findOrThrow(id);
        if (template.getArchivoJasperNombre() == null) {
            throw new IllegalStateException("Esta plantilla no tiene archivo JASPER precompilado");
        }
        return storageService.readFile(template.getArchivoJasperNombre());
    }

    // ── Vista previa ──────────────────────────────────────────────────────────

    /**
     * Genera un PDF de vista previa con datos de muestra usando la plantilla almacenada.
     */
    public byte[] previewPlantilla(Long id) throws Exception {
        ReportTemplate template = findOrThrow(id);

        if (template.getArchivoJrxmlNombre() != null) {
            byte[] jrxmlBytes = storageService.readFile(template.getArchivoJrxmlNombre());
            return jasperService.generarPreview(jrxmlBytes, template.getTipoReporte().name());
        } else if (template.getArchivoJasperNombre() != null) {
            byte[] jasperBytes = storageService.readFile(template.getArchivoJasperNombre());
            return jasperService.generarPreviewDesdeJasperBytes(jasperBytes, template.getTipoReporte().name());
        } else {
            throw new IllegalStateException("Esta plantilla no tiene archivos de reporte asociados");
        }
    }

    // ── Actualización ─────────────────────────────────────────────────────────

    @Transactional
    public ReportTemplateDto actualizarMetadata(Long id, ReportTemplateDto dto) {
        ReportTemplate template = findOrThrow(id);
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            template.setNombre(dto.getNombre());
        }
        if (dto.getDescripcion() != null) {
            template.setDescripcion(dto.getDescripcion());
        }
        if (dto.getTipoReporte() != null) {
            template.setTipoReporte(parseTipo(dto.getTipoReporte()));
        }
        if (dto.getModulo() != null && !dto.getModulo().isBlank()) {
            try { template.setModulo(ReportTemplate.ModuloReporte.valueOf(dto.getModulo().toUpperCase())); } catch (Exception ignored) {}
        }
        if (dto.getTipoDocumento() != null && !dto.getTipoDocumento().isBlank()) {
            try { template.setTipoDocumento(ReportTemplate.TipoDocumento.valueOf(dto.getTipoDocumento().toUpperCase())); } catch (Exception ignored) {}
        }
        if (dto.getCodigo() != null && !dto.getCodigo().isBlank()) {
            template.setCodigo(dto.getCodigo().toUpperCase().replace(' ', '_'));
        }
        if (dto.getVersion() != null && !dto.getVersion().isBlank()) {
            template.setVersion(dto.getVersion());
        }
        template.setFechaActualizacion(LocalDateTime.now());
        return toDto(repository.save(template));
    }

    @Transactional
    public ReportTemplateDto cambiarEstado(Long id, boolean activo) {
        ReportTemplate template = findOrThrow(id);
        template.setActivo(activo);
        template.setFechaActualizacion(LocalDateTime.now());
        return toDto(repository.save(template));
    }

    // ── Eliminación ───────────────────────────────────────────────────────────

    @Transactional
    public void eliminar(Long id) throws IOException {
        ReportTemplate template = findOrThrow(id);
        if (Boolean.TRUE.equals(template.getEsSistema())) {
            throw new IllegalStateException("No se pueden eliminar las plantillas del sistema");
        }
        // Borrar archivos del almacenamiento
        if (template.getArchivoJrxmlNombre() != null) {
            storageService.deleteFile(template.getArchivoJrxmlNombre());
        }
        if (template.getArchivoJasperNombre() != null) {
            storageService.deleteFile(template.getArchivoJasperNombre());
        }
        repository.delete(template);
        log.info("Plantilla de reporte eliminada: id={}", id);
    }

    // ── Conversión ────────────────────────────────────────────────────────────

    private ReportTemplateDto toDto(ReportTemplate t) {
        ReportTemplateDto dto = new ReportTemplateDto();
        dto.setId(t.getId());
        dto.setNombre(t.getNombre());
        dto.setCodigo(t.getCodigo());
        dto.setModulo(t.getModulo() != null ? t.getModulo().name() : null);
        dto.setTipoDocumento(t.getTipoDocumento() != null ? t.getTipoDocumento().name() : null);
        dto.setVersion(t.getVersion());
        dto.setTipoReporte(t.getTipoReporte() != null ? t.getTipoReporte().name() : null);
        dto.setDescripcion(t.getDescripcion());
        dto.setActivo(Boolean.TRUE.equals(t.getActivo()));
        dto.setFechaCreacion(t.getFechaCreacion() != null ? t.getFechaCreacion().format(FMT) : null);
        dto.setFechaActualizacion(t.getFechaActualizacion() != null ? t.getFechaActualizacion().format(FMT) : null);
        dto.setCreadoPor(t.getCreadoPor());
        dto.setArchivoJrxmlNombre(t.getArchivoJrxmlNombre());
        dto.setArchivoJasperNombre(t.getArchivoJasperNombre());
        dto.setTieneJrxml(t.getArchivoJrxmlNombre() != null && storageService.fileExists(t.getArchivoJrxmlNombre()));
        dto.setTieneJasper(t.getArchivoJasperNombre() != null && storageService.fileExists(t.getArchivoJasperNombre()));
        dto.setEsSistema(Boolean.TRUE.equals(t.getEsSistema()));
        return dto;
    }

    private ReportTemplate findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada con id: " + id));
    }

    private ReportTemplate.TipoReporte parseTipo(String tipo) {
        try {
            return ReportTemplate.TipoReporte.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de reporte inválido: " + tipo);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

}

