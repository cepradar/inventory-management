package com.inventory.repository;

import com.inventory.model.MarcaElectrodomestico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarcaElectrodomesticoRepository extends JpaRepository<MarcaElectrodomestico, Long> {
    Optional<MarcaElectrodomestico> findByNombre(String nombre);
}
