package com.inventory.repository;

import com.inventory.model.MovimientoProducto;
import com.inventory.model.Product;
import com.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoProductoRepository extends JpaRepository<MovimientoProducto, Long> {
    
    List<MovimientoProducto> findByProduct(Product product);
    
    List<MovimientoProducto> findByUsuario(User usuario);
    
    List<MovimientoProducto> findByTipo(String tipo);
    
    @Query("SELECT m FROM MovimientoProducto m WHERE m.product.id = :productId ORDER BY m.fecha DESC")
    List<MovimientoProducto> findMovimientosByProductId(@Param("productId") String productId);
    
    @Query("SELECT m FROM MovimientoProducto m WHERE m.usuario.username = :username ORDER BY m.fecha DESC")
    List<MovimientoProducto> findMovimientosByUsuarioUsername(@Param("username") String username);
    
    @Query("SELECT m FROM MovimientoProducto m WHERE m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<MovimientoProducto> findMovimientosByFechaRango(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                         @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT m FROM MovimientoProducto m WHERE m.product.id = :productId AND m.tipo = :tipo ORDER BY m.fecha DESC")
    List<MovimientoProducto> findMovimientosByProductAndTipo(@Param("productId") String productId, 
                                                              @Param("tipo") String tipo);
}
