package com.inventory.repository;

import com.inventory.model.VentaDetalle;
import com.inventory.model.Product;
import com.inventory.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {
    // Buscar detalles por producto
    List<VentaDetalle> findByProduct(Product product);

    // Buscar detalles por venta
    List<VentaDetalle> findByVenta(Venta venta);

    // Buscar detalles por id de producto
    @Query("SELECT vd FROM VentaDetalle vd WHERE vd.product.id = :productId")
    List<VentaDetalle> findByProductoId(@Param("productId") String productId);

    // Buscar detalles por id de venta
    @Query("SELECT vd FROM VentaDetalle vd WHERE vd.venta.id = :ventaId")
    List<VentaDetalle> findByVentaId(@Param("ventaId") Long ventaId);
}
