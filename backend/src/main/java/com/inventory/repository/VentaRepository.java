package com.inventory.repository;

import com.inventory.model.Venta;
import com.inventory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    

    
    List<Venta> findByUsuario(User usuario);
    
    @Query("SELECT DISTINCT v FROM Venta v JOIN v.detalles vd WHERE vd.product.id = :productId ORDER BY v.fecha DESC")
    List<Venta> findVentasByProductId(@Param("productId") String productId);
    
    @Query("SELECT v FROM Venta v WHERE v.usuario.username = :username ORDER BY v.fecha DESC")
    List<Venta> findVentasByUsuarioUsername(@Param("username") String username);
    
    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY v.fecha DESC")
    List<Venta> findVentasByFechaRango(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                      @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT v FROM Venta v WHERE LOWER(CONCAT(v.cliente.nombre, ' ', v.cliente.apellido)) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY v.fecha DESC")
    List<Venta> findVentasByNombreComprador(@Param("nombre") String nombre);

    List<Venta> findByOrdenDeServicioId(String ordenDeServicioId);
}
