package com.inventory.service;

import com.inventory.dto.VentaDto;
import com.inventory.dto.VentaDetalleDto;
import com.inventory.dto.VentaDetalleRegistroDto;
import com.inventory.dto.VentaRegistroDto;
import com.inventory.model.Product;
import com.inventory.model.User;
import com.inventory.model.Venta;
import com.inventory.model.VentaDetalle;
import com.inventory.model.OrdenDeServicio;
import com.inventory.repository.VentaRepository;
import com.inventory.repository.VentaDetalleRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.UserRepository;
import com.inventory.repository.OrdenDeServicioRepository;
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

    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;

    @Autowired
    private OrdenDeServicioRepository ordenDeServicioRepository;

    /**
     * Valida que el técnico autenticado tenga la orden asignada antes de registrar una venta.
     */
    public void validarAccesoOrdenParaVenta(String ordenId, String username) {
        OrdenDeServicio orden = ordenDeServicioRepository.findById(ordenId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + ordenId));
        User tecnico = orden.getTecnicoAsignado();
        if (tecnico == null || !tecnico.getUsername().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException(
                "No tienes permiso para registrar ventas en esta orden");
        }
    }

    /**
     * Registra una nueva venta y crea automáticamente un movimiento SALIDA
     */
    public VentaDto registrarVenta(VentaRegistroDto registroDto) {
        User usuario = userRepository.findById(Objects.requireNonNull(registroDto.getUsuarioUsername(), "usuarioUsername"))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Venta venta = new Venta();
        venta.setNombreComprador(registroDto.getNombreComprador());
        venta.setTelefonoComprador(registroDto.getTelefonoComprador());
        venta.setEmailComprador(registroDto.getEmailComprador());
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setObservaciones(registroDto.getObservaciones());
        venta.setOrdenDeServicioId(registroDto.getOrdenDeServicioId());
        venta.setTotalVenta(BigDecimal.ZERO);

        if (registroDto.getDetalles() == null || registroDto.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe incluir al menos un detalle");
        }

        VentaDetalleRegistroDto primerDetalle = registroDto.getDetalles().get(0);
        Product primerProducto = productRepository.findById(primerDetalle.getProductId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + primerDetalle.getProductId()));
        venta.setLegacyProduct(primerProducto);
        venta.setLegacyCantidad(primerDetalle.getCantidad());
        venta.setLegacyPrecioUnitario(primerDetalle.getPrecioUnitario());

        // Guardar la venta para obtener el ID
        Venta ventaGuardada = ventaRepository.save(venta);

        BigDecimal totalVenta = BigDecimal.ZERO;
        List<VentaDetalle> detalles = new java.util.ArrayList<>();

        for (VentaDetalleRegistroDto detalleDto : registroDto.getDetalles()) {
            Product producto = productRepository.findById(detalleDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleDto.getProductId()));
            if (producto.getQuantity() < detalleDto.getCantidad()) {
            throw new RuntimeException("Cantidad insuficiente para producto " + producto.getName() + ". Disponible: " + producto.getQuantity());
            }
            // Crear detalle
            VentaDetalle detalle = new VentaDetalle(ventaGuardada, producto, detalleDto.getCantidad(), detalleDto.getPrecioUnitario());
            detalles.add(detalle);
            totalVenta = totalVenta.add(detalle.getSubtotal());

            // Actualizar inventario
            producto.setQuantity(producto.getQuantity() - detalleDto.getCantidad());
            productRepository.save(producto);

            // Registrar auditoría
            Integer cantidadInicial = producto.getQuantity() + detalleDto.getCantidad();
            Integer cantidadFinal = producto.getQuantity();
            auditoriaService.registrarMovimiento(
                producto.getId(),
                cantidadInicial,
                cantidadFinal,
                detalleDto.getPrecioUnitario(),
                detalleDto.getPrecioUnitario(),
                "VC",
                "Venta a cliente: " + registroDto.getNombreComprador(),
                registroDto.getUsuarioUsername(),
                "VENTA-" + ventaGuardada.getId()
            );
        }

        // Asociar los detalles y actualizar el total
        ventaGuardada.setDetalles(detalles);
        ventaGuardada.setTotalVenta(totalVenta);
        ventaRepository.save(ventaGuardada);

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
            // Buscar los detalles de venta que contienen el producto
            List<VentaDetalle> detalles = ventaDetalleRepository.findByProduct(producto);
            // Obtener las ventas únicas asociadas a esos detalles
            List<Venta> ventas = detalles.stream()
                .map(VentaDetalle::getVenta)
                .distinct()
                .collect(Collectors.toList());
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
        List<VentaDetalleDto> detallesDto = venta.getDetalles() != null
            ? venta.getDetalles().stream()
                .map(detalle -> new VentaDetalleDto(
                    detalle.getProduct().getId(),
                    detalle.getProduct().getName(),
                    detalle.getCantidad(),
                    detalle.getPrecioUnitario(),
                    detalle.getSubtotal()
                ))
                .collect(Collectors.toList())
            : java.util.Collections.emptyList();

        VentaDto dto = new VentaDto(
            venta.getId(),
            venta.getTotalVenta(),
            venta.getNombreComprador(),
            venta.getTelefonoComprador(),
            venta.getEmailComprador(),
            venta.getUsuario().getUsername(),
            venta.getUsuario().getFirstName() + " " + venta.getUsuario().getLastName(),
            venta.getFecha(),
            venta.getObservaciones(),
            detallesDto
        );
        dto.setOrdenDeServicioId(venta.getOrdenDeServicioId());
        return dto;
    }

    /**
     * Obtiene las ventas asociadas a una orden de servicio.
     */
    public List<VentaDto> obtenerVentasPorOrden(String ordenId) {
        return ventaRepository.findByOrdenDeServicioId(ordenId).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }
}
