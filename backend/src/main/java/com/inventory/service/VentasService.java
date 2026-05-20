package com.inventory.service;

import com.inventory.dto.VentaDto;
import com.inventory.dto.VentaDetalleDto;
import com.inventory.dto.VentaDetalleRegistroDto;
import com.inventory.dto.VentaRegistroDto;
import com.inventory.model.Cliente;
import com.inventory.model.Product;
import com.inventory.model.Servicio;
import com.inventory.model.User;
import com.inventory.model.Venta;
import com.inventory.model.VentaDetalle;
import com.inventory.model.OrdenDeServicio;
import com.inventory.repository.ClienteRepository;
import com.inventory.repository.VentaRepository;
import com.inventory.repository.VentaDetalleRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.ServicioRepository;
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

    @Autowired private VentaRepository ventaRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private ServicioRepository servicioRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private AuditoriaService auditoriaService;
    @Autowired private VentaDetalleRepository ventaDetalleRepository;
    @Autowired private OrdenDeServicioRepository ordenDeServicioRepository;

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
     * Registra una nueva venta.
     * El cliente se resuelve por FK compuesta (clienteId + clienteTipoDocumento).
     */
    public VentaDto registrarVenta(VentaRegistroDto registroDto) {
        // Resolver usuario desde JWT
        User usuario = userRepository.findById(
                Objects.requireNonNull(registroDto.getUsuarioUsername(), "usuarioUsername"))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Resolver cliente por FK compuesta
        String clienteId  = Objects.requireNonNull(registroDto.getClienteId(), "clienteId");
        String tipoDoc    = Objects.requireNonNull(registroDto.getClienteTipoDocumento(), "clienteTipoDocumento");
        Cliente cliente   = clienteRepository.findByIdAndTipoDocumentoId(clienteId, tipoDoc)
            .orElseThrow(() -> new RuntimeException(
                "Cliente no encontrado: id=" + clienteId + ", tipo=" + tipoDoc));

        if (registroDto.getDetalles() == null || registroDto.getDetalles().isEmpty()) {
            throw new RuntimeException("La venta debe incluir al menos un detalle");
        }

        // Construir venta base
        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setObservaciones(registroDto.getObservaciones());
        venta.setOrdenDeServicioId(registroDto.getOrdenDeServicioId());

        // Guardar para obtener ID antes de crear detalles
        Venta ventaGuardada = ventaRepository.save(venta);

        // Crear detalles — soporta PRODUCTOS (con descuento de stock) y SERVICIOS (sin inventario)
        List<VentaDetalle> detalles = new java.util.ArrayList<>();

        for (VentaDetalleRegistroDto detalleDto : registroDto.getDetalles()) {
            String tipoItem = detalleDto.getTipoItem(); // PRODUCTO | SERVICIO

            if ("SERVICIO".equalsIgnoreCase(tipoItem)) {
                // ── Línea de SERVICIO técnico ─────────────────────────────────
                if (detalleDto.getServicioId() == null) {
                    throw new RuntimeException("servicioId es requerido para ítems de tipo SERVICIO");
                }
                Servicio servicio = servicioRepository.findById(detalleDto.getServicioId())
                    .orElseThrow(() -> new RuntimeException(
                        "Servicio no encontrado: " + detalleDto.getServicioId()));
                if (!servicio.isActivo()) {
                    throw new RuntimeException("El servicio '" + servicio.getNombre() + "' no está activo");
                }

                VentaDetalle detalle = new VentaDetalle(
                    ventaGuardada, servicio, detalleDto.getCantidad(), detalleDto.getPrecioUnitario());
                detalles.add(detalle);
                // Los servicios NO descontan inventario ni generan auditoría de stock.

            } else {
                // ── Línea de PRODUCTO físico (comportamiento original) ────────
                if (detalleDto.getProductId() == null) {
                    throw new RuntimeException("productId es requerido para ítems de tipo PRODUCTO");
                }
                Product producto = productRepository.findById(detalleDto.getProductId())
                    .orElseThrow(() -> new RuntimeException(
                        "Producto no encontrado: " + detalleDto.getProductId()));

                if (producto.getQuantity() < detalleDto.getCantidad()) {
                    throw new RuntimeException("Cantidad insuficiente para producto '"
                        + producto.getName() + "'. Disponible: " + producto.getQuantity());
                }

                VentaDetalle detalle = new VentaDetalle(
                    ventaGuardada, producto, detalleDto.getCantidad(), detalleDto.getPrecioUnitario());
                detalles.add(detalle);

                // Descontar inventario
                int cantidadInicial = producto.getQuantity();
                producto.setQuantity(producto.getQuantity() - detalleDto.getCantidad());
                productRepository.save(producto);

                // Auditoría de movimiento de inventario
                auditoriaService.registrarMovimiento(
                    producto.getId(),
                    cantidadInicial,
                    producto.getQuantity(),
                    detalleDto.getPrecioUnitario(),
                    detalleDto.getPrecioUnitario(),
                    "VC",
                    "Venta a cliente: " + cliente.getNombre() + " " + cliente.getApellido(),
                    registroDto.getUsuarioUsername(),
                    "VENTA-" + ventaGuardada.getId()
                );
            }
        }

        ventaGuardada.setDetalles(detalles);
        ventaRepository.save(ventaGuardada);

        return convertirADto(ventaGuardada);
    }

    /** Obtiene todas las ventas ordenadas por fecha descendente. */
    public List<VentaDto> obtenerTodasVentas() {
        return ventaRepository.findAll().stream()
                .sorted((v1, v2) -> v2.getFecha().compareTo(v1.getFecha()))
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /** Obtiene ventas de un producto específico. */
    public List<VentaDto> obtenerVentasProducto(String productId) {
        Product producto = productRepository.findById(
                Objects.requireNonNull(productId, "productId"))
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return ventaDetalleRepository.findByProduct(producto).stream()
                .map(VentaDetalle::getVenta)
                .distinct()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /** Obtiene ventas realizadas por un usuario específico. */
    public List<VentaDto> obtenerVentasUsuario(String usuarioUsername) {
        User usuario = userRepository.findById(
                Objects.requireNonNull(usuarioUsername, "usuarioUsername"))
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return ventaRepository.findByUsuario(usuario).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /** Obtiene ventas en un rango de fechas. */
    public List<VentaDto> obtenerVentasEnRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findVentasByFechaRango(fechaInicio, fechaFin).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /** Busca ventas cuyo cliente coincide (nombre o apellido) con el texto dado. */
    public List<VentaDto> obtenerVentasPorComprador(String nombre) {
        return ventaRepository.findVentasByNombreComprador(nombre).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /** Obtiene una venta por ID. */
    public VentaDto obtenerVentaPorId(Long ventaId) {
        return ventaRepository.findById(Objects.requireNonNull(ventaId, "ventaId"))
                .map(this::convertirADto)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    /** Obtiene el total de ventas en un rango de fechas. */
    public BigDecimal obtenerTotalVentasEnRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findVentasByFechaRango(fechaInicio, fechaFin).stream()
                .map(Venta::getTotalVenta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Obtiene las ventas asociadas a una orden de servicio. */
    public List<VentaDto> obtenerVentasPorOrden(String ordenId) {
        return ventaRepository.findByOrdenDeServicioId(ordenId).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // ── Conversión entidad → DTO ────────────────────────────────────────────

    private VentaDto convertirADto(Venta venta) {
        List<VentaDetalleDto> detallesDto = venta.getDetalles() != null
            ? venta.getDetalles().stream()
                .map(d -> {
                    String tipoItem = d.getTipoItem() != null ? d.getTipoItem() : "PRODUCTO";
                    if ("SERVICIO".equals(tipoItem) && d.getServicio() != null) {
                        // Línea de servicio técnico
                        return new VentaDetalleDto(
                            null, null,
                            d.getServicio().getId(),
                            d.getServicio().getNombre(),
                            "SERVICIO",
                            d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
                    } else if (d.getProduct() != null) {
                        // Línea de producto físico
                        return new VentaDetalleDto(
                            d.getProduct().getId(),
                            d.getProduct().getName(),
                            null, null,
                            "PRODUCTO",
                            d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
                    } else {
                        // Datos inconsistentes — devolver vacío con lo que haya
                        return new VentaDetalleDto(null, "(ítem desconocido)",
                            null, null, tipoItem,
                            d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
                    }
                })
                .collect(Collectors.toList())
            : java.util.Collections.emptyList();

        Cliente c = venta.getCliente();
        String nombreComprador  = c != null
            ? (c.getNombre() + " " + c.getApellido()).trim() : "";
        String telefonoComprador = c != null ? (c.getTelefono() != null ? c.getTelefono() : "") : "";
        String emailComprador    = c != null ? (c.getEmail()    != null ? c.getEmail()    : "") : "";

        VentaDto dto = new VentaDto(
            venta.getId(),
            venta.getTotalVenta(),
            nombreComprador,
            telefonoComprador,
            emailComprador,
            venta.getUsuario().getUsername(),
            venta.getUsuario().getFirstName() + " " + venta.getUsuario().getLastName(),
            venta.getFecha(),
            venta.getObservaciones(),
            detallesDto
        );
        dto.setOrdenDeServicioId(venta.getOrdenDeServicioId());
        if (c != null) {
            dto.setClienteId(c.getId());
            dto.setClienteTipoDocumento(c.getTipoDocumentoId());
        }
        return dto;
    }
}

