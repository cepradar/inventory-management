package com.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import com.inventory.model.Cliente;
import com.inventory.model.ClienteId;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, ClienteId> {
    @NonNull
    Optional<Cliente> findByIdAndTipoDocumentoId(@NonNull String id, @NonNull String tipoDocumentoId);

    @NonNull
    @Query("SELECT c FROM Cliente c WHERE c.id = ?1")
    List<Cliente> findByDocumento(@NonNull String documento);
}
