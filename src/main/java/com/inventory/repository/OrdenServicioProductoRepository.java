package com.inventory.repository;

import com.inventory.model.OrdenServicioProducto;
import com.inventory.model.OrdenServicioProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenServicioProductoRepository extends JpaRepository<OrdenServicioProducto, OrdenServicioProductoId> {
    
    List<OrdenServicioProducto> findByServicioReparacionId(String servicioReparacionId);
    
}
