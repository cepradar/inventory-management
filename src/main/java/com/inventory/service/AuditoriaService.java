package com.inventory.service;

import com.inventory.dto.MovimientoProductoDto;
import com.inventory.model.MovimientoProducto;
import com.inventory.model.Product;
import com.inventory.model.User;
import com.inventory.repository.MovimientoProductoRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditoriaService {

    @Autowired
    private MovimientoProductoRepository movimientoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Registra un nuevo movimiento de producto
     */
    public MovimientoProductoDto registrarMovimiento(String productId, Integer cantidad, String tipo, 
                                                      String descripcion, String usuarioUsername, String referencia) {
        Product producto = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User usuario = userRepository.findById(usuarioUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        MovimientoProducto movimiento = new MovimientoProducto(producto, cantidad, tipo, 
                                                                descripcion, usuario, referencia);
        MovimientoProducto guardado = movimientoRepository.save(movimiento);
        
        return convertirADto(guardado);
    }

    /**
     * Obtiene todos los movimientos
     */
    public List<MovimientoProductoDto> obtenerTodosMovimientos() {
        List<MovimientoProducto> movimientos = movimientoRepository.findAll();
        return movimientos.stream()
                .sorted((m1, m2) -> m2.getFecha().compareTo(m1.getFecha()))
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos de un producto específico
     */
    public List<MovimientoProductoDto> obtenerMovimientosProducto(String productId) {
        Product producto = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        List<MovimientoProducto> movimientos = movimientoRepository.findByProduct(producto);
        return movimientos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos realizados por un usuario
     */
    public List<MovimientoProductoDto> obtenerMovimientosUsuario(String usuarioUsername) {
        User usuario = userRepository.findById(usuarioUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<MovimientoProducto> movimientos = movimientoRepository.findByUsuario(usuario);
        return movimientos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos por tipo (INGRESO o SALIDA)
     */
    public List<MovimientoProductoDto> obtenerMovimientosPorTipo(String tipo) {
        List<MovimientoProducto> movimientos = movimientoRepository.findByTipo(tipo);
        return movimientos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene movimientos en un rango de fechas
     */
    public List<MovimientoProductoDto> obtenerMovimientosEnRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<MovimientoProducto> movimientos = movimientoRepository.findMovimientosByFechaRango(fechaInicio, fechaFin);
        return movimientos.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un movimiento por ID
     */
    public MovimientoProductoDto obtenerMovimientoPorId(Long movimientoId) {
        Optional<MovimientoProducto> movimiento = movimientoRepository.findById(movimientoId);
        return movimiento.map(this::convertirADto)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado"));
    }

    /**
     * Convierte una entidad MovimientoProducto a DTO
     */
    private MovimientoProductoDto convertirADto(MovimientoProducto movimiento) {
        return new MovimientoProductoDto(
                movimiento.getId(),
                Long.parseLong(movimiento.getProduct().getId()),
                movimiento.getProduct().getName(),
                movimiento.getCantidad(),
                movimiento.getTipo(),
                movimiento.getDescripcion(),
                movimiento.getUsuario().getUsername(),
                movimiento.getUsuario().getFirstName() + " " + movimiento.getUsuario().getLastName(),
                movimiento.getFecha(),
                movimiento.getReferencia()
        );
    }
}
