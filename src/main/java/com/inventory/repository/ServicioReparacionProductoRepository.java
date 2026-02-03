package com.inventory.repository;

import com.inventory.model.ServicioReparacionProducto;
import com.inventory.model.ServicioReparacionProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioReparacionProductoRepository extends JpaRepository<ServicioReparacionProducto, ServicioReparacionProductoId> {
    
    List<ServicioReparacionProducto> findByServicioReparacionId(String servicioReparacionId);
    
}
