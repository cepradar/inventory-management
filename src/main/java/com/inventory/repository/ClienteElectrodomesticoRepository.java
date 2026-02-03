package com.inventory.repository;

import com.inventory.model.ClienteElectrodomestico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteElectrodomesticoRepository extends JpaRepository<ClienteElectrodomestico, Long> {
    
    Optional<ClienteElectrodomestico> findByNumeroSerie(String numeroSerie);
    
    List<ClienteElectrodomestico> findByClienteId(String clienteId);
    
    List<ClienteElectrodomestico> findByEstado(String estado);
    
    List<ClienteElectrodomestico> findByClienteIdAndEstado(String clienteId, String estado);
    
    @Query("SELECT ce FROM ClienteElectrodomestico ce WHERE ce.cliente.id = :clienteId AND ce.garantiaVigente = true")
    List<ClienteElectrodomestico> findConGarantiaVigenteByCliente(@Param("clienteId") String clienteId);
    
    @Query("SELECT ce FROM ClienteElectrodomestico ce WHERE ce.usuario.username = :username")
    List<ClienteElectrodomestico> findByUsuarioUsername(@Param("username") String username);
    
    @Query("SELECT ce FROM ClienteElectrodomestico ce WHERE ce.fechaVencimientoGarantia BETWEEN :desde AND :hasta AND ce.garantiaVigente = true")
    List<ClienteElectrodomestico> findGarantiasPorVencer(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
    
    // Validar unicidad de serial + marca + cliente
    boolean existsByNumeroSerieAndMarcaElectrodomesticoIdAndClienteId(String numeroSerie, Long marcaElectrodomesticoId, String clienteId);
}
