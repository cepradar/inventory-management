package com.inventory.repository;

import com.inventory.model.EventosDeProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventosDeProductoRepository extends JpaRepository<EventosDeProducto, EventosDeProducto> {
    List<EventosDeProducto> findByUsuarioUsername(String username);
    List<EventosDeProducto> findByProductoId(Long productoId);
    List<EventosDeProducto> findByTipoDeEventoId(Long tipoDeEventoId);
}
