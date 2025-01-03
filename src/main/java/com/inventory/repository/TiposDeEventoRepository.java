package com.inventory.repository;

import com.inventory.model.TiposDeEvento;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiposDeEventoRepository extends JpaRepository<TiposDeEvento, Long> {
    Optional<TiposDeEvento> findByNombre(String nombre);
}