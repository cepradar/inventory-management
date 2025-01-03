package com.inventory.repository;

import com.inventory.dto.ProductosDto;
import com.inventory.model.CategoriasDeProducto;
import com.inventory.model.Productos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Long> {
    List<Productos> findByCategory(CategoriasDeProducto category);
}