package com.inventory.service;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.dto.OrdenServicioProductoDto;
import com.inventory.model.Cliente;
import com.inventory.model.ClienteElectrodomestico;
import com.inventory.model.Product;
import com.inventory.model.OrdenDeServicio;
import com.inventory.model.OrdenServicioProducto;
import com.inventory.model.User;
import com.inventory.repository.ClienteElectrodomesticoRepository;
import com.inventory.repository.ClienteRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.OrdenDeServicioRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrdenDeServicioService {

    @Autowired
    private OrdenDeServicioRepository servicioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClienteElectrodomesticoRepository clienteElectrodomesticoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrdenDeServicioDto registrarServicio(OrdenDeServicioDto dto, String usernameLogeado) {
        if (dto.getClienteId() == null || dto.getClienteTipoDocumentoId() == null) {
            throw new RuntimeException("Cliente y tipo documento son obligatorios");
        }
        if (dto.getElectrodomesticoId() == null) {
            throw new RuntimeException("Debe seleccionar un electrodoméstico");
        }

        User usuario = userRepository.findById(Objects.requireNonNull(usernameLogeado, "usernameLogeado"))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usernameLogeado));

        Cliente cliente = clienteRepository.findByIdAndTipoDocumentoId(
                Objects.requireNonNull(dto.getClienteId(), "clienteId"),
                Objects.requireNonNull(dto.getClienteTipoDocumentoId(), "clienteTipoDocumentoId")
            )
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dto.getClienteId()));

        ClienteElectrodomestico ce = clienteElectrodomesticoRepository.findById(
                Objects.requireNonNull(dto.getElectrodomesticoId(), "electrodomesticoId")
            )
                .orElseThrow(() -> new RuntimeException("ClienteElectrodomestico no encontrado: " + dto.getElectrodomesticoId()));

        if (!ce.getCliente().getId().equals(cliente.getId()) ||
            !ce.getCliente().getTipoDocumentoId().equals(cliente.getTipoDocumentoId())) {
            throw new RuntimeException("El electrodoméstico no pertenece al cliente indicado");
        }

        OrdenDeServicio servicio = new OrdenDeServicio();
        servicio.setId(generarConsecutivo());
        servicio.setCliente(cliente);
        servicio.setClienteElectrodomestico(ce);
        servicio.setTipoServicio(dto.getTipoServicio());
        servicio.setDescripcionProblema(dto.getDescripcionProblema());
        servicio.setDiagnostico(dto.getDiagnostico());
        servicio.setSolucion(dto.getSolucion());
        servicio.setPartesCambiadas(dto.getPartesCambiadas());
        servicio.setCostoServicio(dto.getCostoServicio() != null ? dto.getCostoServicio() : BigDecimal.ZERO);
        servicio.setCostoRepuestos(dto.getCostoRepuestos() != null ? dto.getCostoRepuestos() : BigDecimal.ZERO);
        servicio.setTotalCosto(servicio.getCostoServicio().add(servicio.getCostoRepuestos()));
        servicio.setGarantiaServicio(dto.getGarantiaServicio() != null ? dto.getGarantiaServicio() : 30);
        servicio.setUsuario(usuario);
        servicio.setObservaciones(dto.getObservaciones());

        // Asignar técnico si se proporciona
        if (dto.getTecnicoAsignadoUsername() != null && !dto.getTecnicoAsignadoUsername().isEmpty()) {
            User tecnico = userRepository.findById(Objects.requireNonNull(dto.getTecnicoAsignadoUsername(), "tecnicoAsignadoUsername"))
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado: " + dto.getTecnicoAsignadoUsername()));
            servicio.setTecnicoAsignado(tecnico);
        }

        // Procesar productos
        if (dto.getProductos() == null || dto.getProductos().isEmpty()) {
            throw new RuntimeException("Debe agregar al menos un producto");
        }

        boolean tieneServicio = false;
        int regProd = 1;
        for (OrdenServicioProductoDto productoDto : dto.getProductos()) {
            Product producto = productRepository.findById(Objects.requireNonNull(productoDto.getProductId(), "productId"))
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + productoDto.getProductId()));

            if (producto.getCategory() != null && "S".equalsIgnoreCase(producto.getCategory().getId())) {
                tieneServicio = true;
            }

            OrdenServicioProducto srp = new OrdenServicioProducto();
            srp.setProducto(producto);
            srp.setCantidad(productoDto.getCantidad() != null ? productoDto.getCantidad() : 1);
            srp.setPrecioUnitario(productoDto.getPrecioUnitario() != null ? productoDto.getPrecioUnitario() : BigDecimal.valueOf(producto.getPrice()));
            srp.setSubtotal(srp.getPrecioUnitario().multiply(new BigDecimal(srp.getCantidad())));
            srp.setRegProd(regProd++);

            servicio.agregarProducto(srp);
        }

        if (!tieneServicio) {
            throw new RuntimeException("Debe agregar al menos un producto de tipo SERVICIO (S)");
        }

        OrdenDeServicio guardado = servicioRepository.save(servicio);
        return convertirADto(guardado);
    }

    public OrdenDeServicioDto actualizarServicio(String id, OrdenDeServicioDto dto) {
        OrdenDeServicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio de reparación no encontrado: " + id));

        if (dto.getTipoServicio() != null) {
            servicio.setTipoServicio(dto.getTipoServicio());
        }
        if (dto.getDescripcionProblema() != null) {
            servicio.setDescripcionProblema(dto.getDescripcionProblema());
        }
        if (dto.getDiagnostico() != null) {
            servicio.setDiagnostico(dto.getDiagnostico());
        }
        if (dto.getSolucion() != null) {
            servicio.setSolucion(dto.getSolucion());
        }
        if (dto.getPartesCambiadas() != null) {
            servicio.setPartesCambiadas(dto.getPartesCambiadas());
        }
        if (dto.getCostoServicio() != null || dto.getCostoRepuestos() != null) {
            BigDecimal costoServicio = dto.getCostoServicio() != null
                    ? dto.getCostoServicio()
                    : servicio.getCostoServicio();
            BigDecimal costoRepuestos = dto.getCostoRepuestos() != null
                    ? dto.getCostoRepuestos()
                    : servicio.getCostoRepuestos();
            servicio.setCostoServicio(costoServicio != null ? costoServicio : BigDecimal.ZERO);
            servicio.setCostoRepuestos(costoRepuestos != null ? costoRepuestos : BigDecimal.ZERO);
            servicio.setTotalCosto(servicio.getCostoServicio().add(servicio.getCostoRepuestos()));
        }
        if (dto.getGarantiaServicio() != null) {
            servicio.setGarantiaServicio(dto.getGarantiaServicio());
        }
        if (dto.getFechaSalida() != null) {
            servicio.setFechaSalida(dto.getFechaSalida());
        }
        if (dto.getVencimientoGarantia() != null) {
            servicio.setVencimientoGarantia(dto.getVencimientoGarantia());
        }
        if (dto.getObservaciones() != null) {
            servicio.setObservaciones(dto.getObservaciones());
        }
        if (dto.getEstado() != null) {
            servicio.setEstado(dto.getEstado());
        }
        if (dto.getTecnicoAsignadoUsername() != null) {
            String tecnicoUsername = dto.getTecnicoAsignadoUsername().trim();
            if (tecnicoUsername.isEmpty()) {
                servicio.setTecnicoAsignado(null);
            } else {
                User tecnico = userRepository.findById(Objects.requireNonNull(tecnicoUsername, "tecnicoAsignadoUsername"))
                        .orElseThrow(() -> new RuntimeException("Técnico no encontrado: " + tecnicoUsername));
                servicio.setTecnicoAsignado(tecnico);
            }
        }

        OrdenDeServicio actualizado = servicioRepository.save(servicio);
        return convertirADto(actualizado);
    }

    public OrdenDeServicioDto obtenerServicioPorId(String id) {
        OrdenDeServicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio de reparación no encontrado: " + id));
        return convertirADto(servicio);
    }

    public List<OrdenDeServicioDto> obtenerServiciosPorCliente(String clienteId) {
        return servicioRepository.findByClienteId(clienteId).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public List<OrdenDeServicioDto> obtenerServiciosPorCliente(String clienteId, String clienteTipoDocumentoId) {
        return servicioRepository.findByClienteIdAndTipoDocumentoId(clienteId, clienteTipoDocumentoId).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public List<OrdenDeServicioDto> obtenerServiciosPorClienteElectrodomestico(Long clienteElectroId) {
        return servicioRepository.findByClienteElectrodomesticoId(clienteElectroId).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public List<OrdenDeServicioDto> obtenerTodosServicios() {
        return servicioRepository.findAll().stream()
                .map(this::convertirADto)
                .sorted((a, b) -> b.getFechaIngreso().compareTo(a.getFechaIngreso()))
                .collect(Collectors.toList());
    }

    public OrdenDeServicioDto cambiarEstado(String id, String nuevoEstado) {
        OrdenDeServicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio de reparación no encontrado: " + id));

        servicio.setEstado(nuevoEstado);

        if ("LISTO".equalsIgnoreCase(nuevoEstado) && servicio.getGarantiaServicio() != null) {
            servicio.setVencimientoGarantia(LocalDate.now().plusDays(servicio.getGarantiaServicio()));
        }

        if ("ENTREGADO".equalsIgnoreCase(nuevoEstado)) {
            servicio.setFechaSalida(LocalDateTime.now());
        }

        OrdenDeServicio actualizado = servicioRepository.save(servicio);
        return convertirADto(actualizado);
    }

    public void eliminarServicio(String id) {
        OrdenDeServicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio de reparación no encontrado: " + id));
        servicioRepository.delete(servicio);
    }

    public List<OrdenDeServicioDto> obtenerServiciosPendientes() {
        return servicioRepository.findServiciosPendientes().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public List<OrdenDeServicioDto> obtenerGarantiasPorVencer(LocalDate desde, LocalDate hasta) {
        return servicioRepository.findGarantiasPorVencer(desde, hasta).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    private OrdenDeServicioDto convertirADto(OrdenDeServicio servicio) {
        OrdenDeServicioDto dto = new OrdenDeServicioDto();
        dto.setId(servicio.getId());
        dto.setClienteId(servicio.getCliente() != null ? servicio.getCliente().getId() : null);
        dto.setClienteTipoDocumentoId(servicio.getCliente() != null ? servicio.getCliente().getTipoDocumentoId() : null);
        dto.setClienteNombre(servicio.getCliente() != null ? servicio.getCliente().getNombre() : null);
        dto.setClienteApellido(servicio.getCliente() != null ? servicio.getCliente().getNombre() : null);
        dto.setElectrodomesticoId(servicio.getClienteElectrodomestico() != null ? servicio.getClienteElectrodomestico().getId() : null);
        dto.setElectrodomesticoTipo(servicio.getClienteElectrodomestico() != null ? servicio.getClienteElectrodomestico().getElectrodomesticoTipo() : null);
        dto.setElectrodomesticoMarca(servicio.getClienteElectrodomestico() != null && servicio.getClienteElectrodomestico().getMarcaElectrodomestico() != null ? servicio.getClienteElectrodomestico().getMarcaElectrodomestico().getNombre() : null);
        dto.setElectrodomesticoModelo(servicio.getClienteElectrodomestico() != null ? servicio.getClienteElectrodomestico().getElectrodomesticoModelo() : null);
        dto.setTipoServicio(servicio.getTipoServicio());
        dto.setDescripcionProblema(servicio.getDescripcionProblema());
        dto.setDiagnostico(servicio.getDiagnostico());
        dto.setSolucion(servicio.getSolucion());
        dto.setPartesCambiadas(servicio.getPartesCambiadas());
        dto.setCostoServicio(servicio.getCostoServicio());
        dto.setCostoRepuestos(servicio.getCostoRepuestos());
        dto.setTotalCosto(servicio.getTotalCosto());
        dto.setEstado(servicio.getEstado());
        dto.setFechaIngreso(servicio.getFechaIngreso());
        dto.setFechaSalida(servicio.getFechaSalida());
        dto.setGarantiaServicio(servicio.getGarantiaServicio());
        dto.setVencimientoGarantia(servicio.getVencimientoGarantia());
        dto.setUsuarioUsername(servicio.getUsuario() != null ? servicio.getUsuario().getUsername() : null);
        dto.setUsuarioNombre(servicio.getUsuario() != null ? servicio.getUsuario().getFirstName() + " " + servicio.getUsuario().getLastName() : null);
        dto.setTecnicoAsignadoUsername(servicio.getTecnicoAsignado() != null ? servicio.getTecnicoAsignado().getUsername() : null);
        dto.setTecnicoAsignadoNombre(servicio.getTecnicoAsignado() != null ? servicio.getTecnicoAsignado().getFirstName() + " " + servicio.getTecnicoAsignado().getLastName() : null);
        dto.setObservaciones(servicio.getObservaciones());
        
        // Convertir productos
        List<OrdenServicioProductoDto> productosDto = servicio.getProductos().stream()
                .map(srp -> {
                    String claveCompuesta = generarClaveCompuesta(
                        servicio.getId(),
                        servicio.getFechaIngreso(),
                        servicio.getCliente().getId(),
                        servicio.getCliente().getTipoDocumentoId(),
                        srp.getRegProd()
                    );
                    Product producto = srp.getProducto();
                    String productId = producto != null ? producto.getId() : null;
                    String productName = producto != null ? producto.getName() : "[Producto eliminado]";
                    return new OrdenServicioProductoDto(
                        servicio.getId(),
                        productId,
                        productName,
                        srp.getCantidad(),
                        srp.getRegProd(),
                        srp.getPrecioUnitario(),
                        srp.getSubtotal(),
                        claveCompuesta
                    );
                })
                .collect(Collectors.toList());
        dto.setProductos(productosDto);
        
        return dto;
    }

    private String generarConsecutivo() {
        String ultimoId = servicioRepository.findUltimoId();
        int siguiente = 1;
        
        if (ultimoId != null && !ultimoId.isEmpty()) {
            try {
                siguiente = Integer.parseInt(ultimoId) + 1;
            } catch (NumberFormatException e) {
                siguiente = 1;
            }
        }
        
        return String.format("%06d", siguiente);
    }

    private String generarClaveCompuesta(String ordenId, LocalDateTime fechaOrden, String clienteId, String clienteTipoDocumentoId, Integer regProd) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        
        String fecha = fechaOrden.format(dateFormatter);
        String hora = fechaOrden.format(timeFormatter);
        
        return String.format("%s-%s-%s-%s-%s-%03d", ordenId, fecha, hora, clienteId, clienteTipoDocumentoId, regProd);
    }
}
