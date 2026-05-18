package com.inventory.repository;

import com.inventory.model.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findAllByOrderByFechaCreacionDesc();

    /** Legacy: búsqueda por tipo enum legacy */
    List<ReportTemplate> findByTipoReporte(ReportTemplate.TipoReporte tipoReporte);

    List<ReportTemplate> findByActivoTrue();

    Optional<ReportTemplate> findByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);

    // ── Nuevas consultas por módulo y tipo de documento ───────────────────────

    /** Busca la plantilla activa por código único (ej: "FACTURA_VENTA") */
    Optional<ReportTemplate> findByCodigoAndActivoTrue(String codigo);

    /** Busca plantillas activas de un módulo */
    List<ReportTemplate> findByModuloAndActivoTrue(ReportTemplate.ModuloReporte modulo);

    /** Busca la plantilla activa de un tipo de documento dentro de un módulo */
    Optional<ReportTemplate> findByModuloAndTipoDocumentoAndActivoTrue(
            ReportTemplate.ModuloReporte modulo,
            ReportTemplate.TipoDocumento tipoDocumento);

    /** Busca todas las plantillas activas de un tipo de documento */
    List<ReportTemplate> findByTipoDocumentoAndActivoTrue(ReportTemplate.TipoDocumento tipoDocumento);
}
