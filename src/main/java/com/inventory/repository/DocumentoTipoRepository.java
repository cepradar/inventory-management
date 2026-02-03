package com.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventory.model.DocumentoTipo;

@Repository
public interface DocumentoTipoRepository extends JpaRepository<DocumentoTipo, String> {
}
