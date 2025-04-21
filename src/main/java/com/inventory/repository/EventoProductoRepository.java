package com.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.model.EventoProducto;

@Repository
public interface EventoProductoRepository extends JpaRepository<EventoProducto, Long> {
    List<EventoProducto> findByProducto_Id(Long productoId);
    List<EventoProducto> findByCliente_Nit(Long nit);
    List<EventoProducto> findByFechaEventoBetween(LocalDateTime desde, LocalDateTime hasta);
}
