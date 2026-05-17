package com.inventory.repository;

import com.inventory.model.TipoEvento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEvento, String> {
    TipoEvento findByNombre(String nombre);

    Optional<TipoEvento> findByIdAndCategoriaId(String id, Long categoriaId);

    Optional<TipoEvento> findByNombreAndCategoriaId(String nombre, Long categoriaId);
}