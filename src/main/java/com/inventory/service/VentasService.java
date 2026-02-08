package com.inventory.service;

import com.inventory.dto.VentaDto;
import com.inventory.model.Product;
import com.inventory.model.User;
import com.inventory.model.Venta;
import com.inventory.repository.VentaRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentasService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Registra una nueva venta y crea automáticamente un movimiento SALIDA
     */
    public VentaDto registrarVenta(String productId, Integer cantidad, BigDecimal precioUnitario, 
                                    String nombreComprador, String telefonoComprador, String emailComprador,
                                    String usuarioUsername, String observaciones) {
        Product producto = productRepository.findById(Objects.requireNonNull(productId, "productId"))
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        User usuario = userRepository.findById(Objects.requireNonNull(usuarioUsername, "usuarioUsername"))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que haya suficiente cantidad
        if (producto.getQuantity() < cantidad) {
            throw new RuntimeException("Cantidad insuficiente. Disponible: " + producto.getQuantity());
        }

        // Crear la venta
        Venta venta = new Venta(producto, cantidad, precioUnitario, nombreComprador, 
                               telefonoComprador, emailComprador, usuario, observaciones);
        Venta ventaGuardada = ventaRepository.save(venta);

        // Reducir la cantidad del producto
        producto.setQuantity(producto.getQuantity() - cantidad);
        productRepository.save(producto);

        // Crear evento de auditoría de VENTA (categoria VENTA)
        BigDecimal precioBase = precioUnitario != null ? precioUnitario : BigDecimal.valueOf(producto.getPrice());
        Integer cantidadInicial = producto.getQuantity() + cantidad;
        Integer cantidadFinal = producto.getQuantity();
        auditoriaService.registrarMovimiento(
                productId,
                cantidadInicial,
                cantidadFinal,
                precioBase,
                precioBase,
                "VC",
                "Venta a cliente: " + nombreComprador,
                usuarioUsername,
                "VENTA-" + ventaGuardada.getId()
        );

        return convertirADto(ventaGuardada);
    }

    /**
     * Obtiene todas las ventas
     */
    public List<VentaDto> obtenerTodasVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        return ventas.stream()
                .sorted((v1, v2) -> v2.getFecha().compareTo(v1.getFecha()))
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene ventas de un producto específico
     */
    public List<VentaDto> obtenerVentasProducto(String productId) {
                Product producto = productRepository.findById(Objects.requireNonNull(productId, "productId"))
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        List<Venta> ventas = ventaRepository.findByProduct(producto);
        return ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene ventas realizadas por un usuario
     */
    public List<VentaDto> obtenerVentasUsuario(String usuarioUsername) {
                User usuario = userRepository.findById(Objects.requireNonNull(usuarioUsername, "usuarioUsername"))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Venta> ventas = ventaRepository.findByUsuario(usuario);
        return ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene ventas en un rango de fechas
     */
    public List<VentaDto> obtenerVentasEnRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Venta> ventas = ventaRepository.findVentasByFechaRango(fechaInicio, fechaFin);
        return ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene ventas por nombre de comprador
     */
    public List<VentaDto> obtenerVentasPorComprador(String nombreComprador) {
        List<Venta> ventas = ventaRepository.findVentasByNombreComprador(nombreComprador);
        return ventas.stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una venta por ID
     */
    public VentaDto obtenerVentaPorId(Long ventaId) {
                Optional<Venta> venta = ventaRepository.findById(Objects.requireNonNull(ventaId, "ventaId"));
        return venta.map(this::convertirADto)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    /**
     * Obtiene el total de ventas en un rango de fechas
     */
    public BigDecimal obtenerTotalVentasEnRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Venta> ventas = ventaRepository.findVentasByFechaRango(fechaInicio, fechaFin);
        return ventas.stream()
                .map(Venta::getTotalVenta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Convierte una entidad Venta a DTO
     */
    private VentaDto convertirADto(Venta venta) {
        return new VentaDto(
                venta.getId(),
                venta.getProduct().getId(),
                venta.getProduct().getName(),
                venta.getCantidad(),
                venta.getPrecioUnitario(),
                venta.getTotalVenta(),
                venta.getNombreComprador(),
                venta.getTelefonoComprador(),
                venta.getEmailComprador(),
                venta.getUsuario().getUsername(),
                venta.getUsuario().getFirstName() + " " + venta.getUsuario().getLastName(),
                venta.getFecha(),
                venta.getObservaciones()
        );
    }
}
