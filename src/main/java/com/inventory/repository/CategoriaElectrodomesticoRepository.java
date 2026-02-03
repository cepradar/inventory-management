package com.inventory.repository;

import com.inventory.model.CategoriaElectrodomestico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaElectrodomesticoRepository extends JpaRepository<CategoriaElectrodomestico, Long> {
    
    Optional<CategoriaElectrodomestico> findByNombre(String nombre);
    
    List<CategoriaElectrodomestico> findByActivoTrue();
}
