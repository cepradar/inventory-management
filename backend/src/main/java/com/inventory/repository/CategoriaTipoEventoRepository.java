package com.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.model.CategoriaTipoEvento;

@Repository
public interface CategoriaTipoEventoRepository extends JpaRepository<CategoriaTipoEvento, Long> {
    Optional<CategoriaTipoEvento> findByNombre(String nombre);
}
