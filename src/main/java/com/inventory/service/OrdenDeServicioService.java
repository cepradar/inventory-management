package com.inventory.service;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.dto.OrdenServicioProductoDto;
import com.inventory.model.Cliente;
import com.inventory.model.ClienteElectrodomestico;
import com.inventory.model.Product;
import com.inventory.model.OrdenDeServicio;
import com.inventory.model.OrdenServicioProducto;
import com.inventory.model.TipoEvento;
import com.inventory.model.User;
import com.inventory.repository.ClienteElectrodomesticoRepository;
import com.inventory.repository.ClienteRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.OrdenDeServicioRepository;
import com.inventory.repository.TipoEventoRepository;
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

    private static final Long CATEGORIA_ORDEN_SERVICIO_ID = 2L;

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

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Autowired
    private AuditoriaService auditoriaService;

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
        TipoEvento eventoCreacion = resolverTipoEventoEstado("RECIBIDO");
        servicio.setEstado(eventoCreacion.getId());
        servicio.setUsuario(usuario);
        servicio.setObservaciones(dto.getObservaciones());

        // Asignar técnico si se proporciona
        if (dto.getTecnicoAsignadoUsername() != null && !dto.getTecnicoAsignadoUsername().isEmpty()) {
            User tecnico = userRepository.findById(Objects.requireNonNull(dto.getTecnicoAsignadoUsername(), "tecnicoAsignadoUsername"))
                    .orElseThrow(() -> new RuntimeException("Técnico no encontrado: " + dto.getTecnicoAsignadoUsername()));
            servicio.setTecnicoAsignado(tecnico);
        }

        // NOTA: Los productos ahora se manejan a través del módulo de Ventas
        // Una orden de servicio puede tener múltiples ventas asociadas
        // ver VentasService.registrarVentaDesdeOrdenServicio()

        OrdenDeServicio guardado = servicioRepository.save(servicio);
        registrarAuditoriaEstado(
            guardado,
            null,
            "RECIBIDO",
            eventoCreacion.getId(),
            usernameLogeado
        );
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
            servicio.setEstado(resolverTipoEventoEstado(dto.getEstado()).getId());
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

    public OrdenDeServicioDto cambiarEstado(String id, String nuevoEstado, String usernameLogeado) {
        OrdenDeServicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Servicio de reparación no encontrado: " + id));

        String estadoAnterior = estadoVisualDesdeCodigo(servicio.getEstado());
        String estadoNormalizado = normalizarEstado(nuevoEstado);
        TipoEvento tipoEvento = resolverTipoEventoEstado(estadoNormalizado);

        servicio.setEstado(tipoEvento.getId());

        if (("LISTO".equalsIgnoreCase(estadoNormalizado) || "REPARADO".equalsIgnoreCase(estadoNormalizado))
                && servicio.getGarantiaServicio() != null) {
            servicio.setVencimientoGarantia(LocalDate.now().plusDays(servicio.getGarantiaServicio()));
        }

        if ("ENTREGADO".equalsIgnoreCase(estadoNormalizado)) {
            servicio.setFechaSalida(LocalDateTime.now());
        }

        OrdenDeServicio actualizado = servicioRepository.save(servicio);

        registrarAuditoriaEstado(
            actualizado,
            estadoAnterior,
            estadoNormalizado,
            tipoEvento.getId(),
            usernameLogeado
        );

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
        dto.setEstado(estadoVisualDesdeCodigo(servicio.getEstado()));
        dto.setFechaIngreso(servicio.getFechaIngreso());
        dto.setFechaSalida(servicio.getFechaSalida());
        dto.setGarantiaServicio(servicio.getGarantiaServicio());
        dto.setVencimientoGarantia(servicio.getVencimientoGarantia());
        dto.setUsuarioUsername(servicio.getUsuario() != null ? servicio.getUsuario().getUsername() : null);
        dto.setUsuarioNombre(servicio.getUsuario() != null ? servicio.getUsuario().getFirstName() + " " + servicio.getUsuario().getLastName() : null);
        dto.setTecnicoAsignadoUsername(servicio.getTecnicoAsignado() != null ? servicio.getTecnicoAsignado().getUsername() : null);
        dto.setTecnicoAsignadoNombre(servicio.getTecnicoAsignado() != null ? servicio.getTecnicoAsignado().getFirstName() + " " + servicio.getTecnicoAsignado().getLastName() : null);
        dto.setObservaciones(servicio.getObservaciones());
        
        // NOTA: Los productos se manejan ahora en el módulo de Ventas
        // Para obtener ventas asociadas a esta orden, usar VentasService.obtenerVentasPorOrdenServicio()
        
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

    private String normalizarEstado(String estado) {
        String estadoNormalizado = Objects.requireNonNull(estado, "estado").trim().toUpperCase();
        if ("REPARADD".equals(estadoNormalizado)) {
            return "REPARADO";
        }
        return estadoNormalizado;
    }

    private String nombreEventoPorEstado(String estado) {
        switch (estado) {
            case "RECIBIDO":
                return "ORDEN_SERVICIO_CREADA";
            case "EN_PROCESO":
                return "ORDEN_SERVICIO_EN_PROCESO";
            case "EN_DIAGNOSTICO":
                return "ORDEN_SERVICIO_DIAGNOSTICADA";
            case "REPARADO":
                return "ORDEN_SERVICIO_REPARADA";
            case "LISTO":
                return "ORDEN_SERVICIO_LISTA";
            case "ENTREGADO":
                return "ORDEN_SERVICIO_ENTREGADA";
            case "CANCELADO":
                return "ORDEN_SERVICIO_CANCELADA";
            default:
                throw new RuntimeException("Estado de orden no soportado para auditoria: " + estado);
        }
    }

    private TipoEvento resolverTipoEventoEstado(String estadoEntrada) {
        String estadoNormalizado = normalizarEstado(estadoEntrada);
        String nombreEvento = nombreEventoPorEstado(estadoNormalizado);
        return tipoEventoRepository.findByNombreAndCategoriaId(nombreEvento, CATEGORIA_ORDEN_SERVICIO_ID)
                .orElseThrow(() -> new RuntimeException(
                        "No existe tipo_evento para estado " + estadoNormalizado + " en categoria_id=2"));
    }

    private String estadoVisualDesdeCodigo(String codigoEstado) {
        if (codigoEstado == null || codigoEstado.isBlank()) {
            return "-";
        }
        TipoEvento tipoEvento = tipoEventoRepository.findById(codigoEstado).orElse(null);
        if (tipoEvento == null || tipoEvento.getNombre() == null) {
            return codigoEstado;
        }

        switch (tipoEvento.getNombre()) {
            case "ORDEN_SERVICIO_CREADA":
                return "RECIBIDO";
            case "ORDEN_SERVICIO_EN_PROCESO":
                return "EN_PROCESO";
            case "ORDEN_SERVICIO_DIAGNOSTICADA":
                return "EN_DIAGNOSTICO";
            case "ORDEN_SERVICIO_REPARADA":
                return "REPARADO";
            case "ORDEN_SERVICIO_LISTA":
                return "LISTO";
            case "ORDEN_SERVICIO_ENTREGADA":
                return "ENTREGADO";
            case "ORDEN_SERVICIO_CANCELADA":
                return "CANCELADO";
            default:
                return tipoEvento.getNombre();
        }
    }

    private void registrarAuditoriaEstado(OrdenDeServicio orden, String estadoAnterior, String estadoNuevo,
                                          String tipoEventoId, String usernameLogeado) {
        String activoId = orden.getClienteElectrodomestico() != null
                ? "CE-" + orden.getClienteElectrodomestico().getId()
                : "OS-" + orden.getId();

        String descripcion = estadoAnterior == null
                ? "Orden creada en estado " + estadoNuevo
                : "Cambio de estado de orden: " + estadoAnterior + " -> " + estadoNuevo;

        String referencia = "ORDEN-" + orden.getId();
        BigDecimal costo = orden.getTotalCosto() != null ? orden.getTotalCosto() : BigDecimal.ZERO;

        auditoriaService.registrarMovimiento(
                activoId,
                0,
                0,
                costo,
                costo,
                tipoEventoId,
                descripcion,
                usernameLogeado,
                referencia
        );
    }
}
