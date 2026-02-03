package com.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import com.inventory.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    @NonNull
    Optional<Cliente> findById(@NonNull String id); 
}
