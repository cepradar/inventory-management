package com.inventory.repository;

import com.inventory.model.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisosRepository extends JpaRepository<Permisos, Long> {
    Optional<Permisos> findByName(String name);
}
