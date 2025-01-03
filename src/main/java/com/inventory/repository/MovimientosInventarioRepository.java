package com.inventory.repository;

import com.inventory.model.MovimientosInventario;
import com.inventory.service.MovimientosInventarioService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientosInventarioRepository extends JpaRepository<MovimientosInventario, Long> {

    MovimientosInventarioService save(MovimientosInventarioService movement);

}
