package com.inventory.repository;

import com.inventory.model.TipoEvento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoEventoRepository extends JpaRepository<TipoEvento, String> {
    TipoEvento findByNombre(String nombre);
}