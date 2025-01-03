package com.inventory.repository;

import com.inventory.model.Usuarios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    Optional<Usuarios> findByUsername(String username);
    
}
