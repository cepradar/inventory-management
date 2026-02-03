package com.inventory.repository;

import com.inventory.model.ServicioReparacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ServicioReparacionRepository extends JpaRepository<ServicioReparacion, String> {

    @Query(value = "SELECT id FROM orden_de_servicio ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String findUltimoId();
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.cliente.id = :clienteId ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByClienteId(String clienteId);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.clienteElectrodomestico.id = :clienteElectrodomesticoId ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByClienteElectrodomesticoId(Long clienteElectrodomesticoId);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.estado = :estado ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByEstado(String estado);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.tipoServicio = :tipoServicio ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByTipoServicio(String tipoServicio);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.usuario.username = :username ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByUsuarioUsername(String username);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.fechaIngreso >= :fechaInicio AND s.fechaIngreso <= :fechaFin ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByFechaIngresoRango(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.estado != 'ENTREGADO' AND s.estado != 'CANCELADO' ORDER BY s.fechaIngreso ASC")
    List<ServicioReparacion> findServiciosPendientes();
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.vencimientoGarantia IS NOT NULL AND s.vencimientoGarantia >= :hoy AND s.vencimientoGarantia <= :proximosDias ORDER BY s.vencimientoGarantia ASC")
    List<ServicioReparacion> findGarantiasPorVencer(LocalDate hoy, LocalDate proximosDias);
    
    @Query("SELECT s FROM ServicioReparacion s WHERE s.cliente.id = :clienteId AND s.estado = :estado ORDER BY s.fechaIngreso DESC")
    List<ServicioReparacion> findByClienteIdAndEstado(String clienteId, String estado);
}
