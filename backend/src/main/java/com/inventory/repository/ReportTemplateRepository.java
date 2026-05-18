package com.inventory.repository;

import com.inventory.model.ReportTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findAllByOrderByFechaCreacionDesc();

    List<ReportTemplate> findByTipoReporte(ReportTemplate.TipoReporte tipoReporte);

    List<ReportTemplate> findByActivoTrue();

    Optional<ReportTemplate> findByNombre(String nombre);

    boolean existsByNombreAndIdNot(String nombre, Long id);
}
