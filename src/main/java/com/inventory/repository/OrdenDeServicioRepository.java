package com.inventory.repository;

import com.inventory.model.OrdenDeServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdenDeServicioRepository extends JpaRepository<OrdenDeServicio, String> {

    @Query(value = "SELECT id FROM orden_de_servicio ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findUltimoId();
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.cliente.id = :clienteId ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByClienteId(String clienteId);

    @Query("SELECT s FROM OrdenDeServicio s WHERE s.cliente.id = :clienteId AND s.cliente.tipoDocumentoId = :clienteTipoDocumentoId ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByClienteIdAndTipoDocumentoId(String clienteId, String clienteTipoDocumentoId);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.clienteElectrodomestico.id = :clienteElectrodomesticoId ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByClienteElectrodomesticoId(Long clienteElectrodomesticoId);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.estado = :estado ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByEstado(String estado);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.tipoServicio = :tipoServicio ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByTipoServicio(String tipoServicio);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.usuario.username = :username ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByUsuarioUsername(String username);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.fechaIngreso >= :fechaInicio AND s.fechaIngreso <= :fechaFin ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByFechaIngresoRango(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.estado != 'ENTREGADO' AND s.estado != 'CANCELADO' ORDER BY s.fechaIngreso ASC")
    List<OrdenDeServicio> findServiciosPendientes();
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.vencimientoGarantia IS NOT NULL AND s.vencimientoGarantia >= :hoy AND s.vencimientoGarantia <= :proximosDias ORDER BY s.vencimientoGarantia ASC")
    List<OrdenDeServicio> findGarantiasPorVencer(LocalDate hoy, LocalDate proximosDias);
    
    @Query("SELECT s FROM OrdenDeServicio s WHERE s.cliente.id = :clienteId AND s.estado = :estado ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByClienteIdAndEstado(String clienteId, String estado);

    @Query("SELECT s FROM OrdenDeServicio s WHERE s.cliente.id = :clienteId AND s.cliente.tipoDocumentoId = :clienteTipoDocumentoId AND s.estado = :estado ORDER BY s.fechaIngreso DESC")
    List<OrdenDeServicio> findByClienteIdAndTipoDocumentoIdAndEstado(String clienteId, String clienteTipoDocumentoId, String estado);
}
