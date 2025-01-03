package com.inventory.repository;

import com.inventory.model.CategoriasDeProducto;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriasDeProductoRepository extends JpaRepository<CategoriasDeProducto, Long> {
    Optional<CategoriasDeProducto> findByName(String name);
}