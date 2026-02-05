package com.inventory.repository;

import com.inventory.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    
    // Buscar por tipo de evento
    List<Auditoria> findByTipoEventoId(String tipoEventoId);
    
    // Buscar por varios tipos de evento
    List<Auditoria> findByTipoEventoIdIn(List<String> tipoEventoIds);
    
    // Buscar por producto
    List<Auditoria> findByProductId(String productId);
    
    // Buscar por usuario
    @Query("SELECT a FROM Auditoria a WHERE UPPER(a.usuario.username) = UPPER(?1)")
    List<Auditoria> findByUsuarioUsernameIgnoreCase(String username);
    
    // Buscar en rango de fechas
    List<Auditoria> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Ordenar por fecha descendente
    List<Auditoria> findAllByOrderByFechaDesc();
    
    // Buscar por categoría de evento
    @Query("SELECT a FROM Auditoria a WHERE a.tipoEvento.categoria.nombre = ?1 ORDER BY a.fecha DESC")
    List<Auditoria> findByTipoEventoCategoria(String categoria);
}
