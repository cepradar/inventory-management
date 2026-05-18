package com.inventory.repository;

import com.inventory.model.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisosRepository extends JpaRepository<Permisos, Long> {

    /** Alias legacy: busca por code (name == code en el modelo actual) */
    @Query("SELECT p FROM Permisos p WHERE p.code = :name")
    Optional<Permisos> findByName(@Param("name") String name);

    Optional<Permisos> findByCode(String code);

    List<Permisos> findByModuleKey(String moduleKey);

    List<Permisos> findByActiveTrue();

    List<Permisos> findByUiVisibleTrueAndActiveTrue();

    /** Catálogo agrupado: códigos activos visible en UI ordenados por módulo+acción */
    @Query("SELECT p FROM Permisos p WHERE p.active = true AND p.uiVisible = true " +
           "ORDER BY p.moduleKey, p.categoryKey, p.actionKey")
    List<Permisos> findCatalog();
}

