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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
        // Regla 1: toda orden nueva se crea con estado ORDEN_SERVICIO_CREADA / SOC
        TipoEvento eventoCreacion = resolverTipoEventoEstado("RECIBIDO");
        servicio.setEstado(eventoCreacion.getId()); // almacena "SOC"
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
        List<OrdenDeServicio> servicios = servicioRepository.findAll();
        // Carga todos los TipoEvento en una sola consulta para evitar el problema N+1
        Map<String, String> mapaEstados = buildMapaEstados();
        return servicios.stream()
                .map(s -> convertirADto(s, mapaEstados))
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

    /**
     * Regla 2: "Asignar Técnico" sólo muestra órdenes en estado ORDEN_SERVICIO_CREADA.
     * La query usa subquery sobre tipo_evento.nombre para ser robusta ante distintos códigos en BD.
     */
    public List<OrdenDeServicioDto> obtenerOrdenesParaAsignar() {
        return servicioRepository.findOrdenesParaAsignar().stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Devuelve las órdenes en estado LISTA o REPARADA, listas para entregar al cliente.
     */
    public List<OrdenDeServicioDto> obtenerOrdenesParaEntregar() {
        Map<String, String> mapaEstados = buildMapaEstados();
        return servicioRepository.findOrdenesParaEntregar().stream()
                .map(s -> convertirADto(s, mapaEstados))
                .collect(Collectors.toList());
    }

    /**
     * Regla 3: asigna un técnico y cambia el estado a ORDEN_SERVICIO_ASIGNADA.
     * Valida que la orden esté en ORDEN_SERVICIO_CREADA antes de asignar.
     * El código de estado se resuelve dinámicamente desde tipo_evento para evitar
     * depender de un valor hardcodeado ("SOC") que puede variar según la BD.
     */
    public OrdenDeServicioDto asignarTecnico(String id, String tecnicoUsername, String usuarioLogeado) {
        OrdenDeServicio servicio = servicioRepository.findById(
                        Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));

        // Resolver dinámicamente el código de ORDEN_SERVICIO_CREADA en la BD
        String codigoCreada = resolverTipoEventoEstado("RECIBIDO").getId();
        if (!codigoCreada.equals(servicio.getEstado())) {
            throw new RuntimeException(
                    "Solo se pueden asignar órdenes en estado ORDEN_SERVICIO_CREADA. " +
                    "Estado actual de la orden " + id + ": " + estadoVisualDesdeCodigo(servicio.getEstado()));
        }

        if (servicio.getTecnicoAsignado() != null) {
            throw new RuntimeException(
                    "La orden " + id + " ya tiene un técnico asignado: " +
                    servicio.getTecnicoAsignado().getUsername());
        }

        User tecnico = userRepository.findById(
                        Objects.requireNonNull(tecnicoUsername, "tecnicoUsername"))
                .orElseThrow(() -> new RuntimeException("Técnico no encontrado: " + tecnicoUsername));

        TipoEvento eventoAsignada = resolverTipoEventoEstado("ASIGNADO");

        servicio.setTecnicoAsignado(tecnico);
        servicio.setEstado(eventoAsignada.getId());
        servicio.setFechaAsignacion(LocalDateTime.now());

        OrdenDeServicio actualizado = servicioRepository.save(servicio);

        registrarAuditoriaEstado(actualizado, "ORDEN_SERVICIO_CREADA", "ORDEN_SERVICIO_ASIGNADA",
                eventoAsignada.getId(), usuarioLogeado);

        return convertirADto(actualizado);
    }

    /**
     * Regla 4: "Responder Orden" sólo muestra órdenes asignadas al técnico autenticado.
     */
    public List<OrdenDeServicioDto> obtenerMisOrdenes(String username) {
        return servicioRepository.findByTecnicoAsignadoUsername(username).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Permite a un técnico (o admin) actualizar los campos técnicos de su orden
     * y cambiar el estado en una sola operación.
     * Si isAdmin=false, valida que la orden esté asignada al técnico autenticado.
     */
    public OrdenDeServicioDto cerrarOrdenPorTecnico(String id, OrdenDeServicioDto cierreDto,
                                                    String nuevoEstado, String usernameLogeado,
                                                    boolean isAdmin) {
        OrdenDeServicio servicio = servicioRepository.findById(Objects.requireNonNull(id, "id"))
                .orElseThrow(() -> new RuntimeException("Orden no encontrada: " + id));

        if (!isAdmin) {
            if (servicio.getTecnicoAsignado() == null ||
                    !servicio.getTecnicoAsignado().getUsername().equals(usernameLogeado)) {
                throw new RuntimeException("Solo puedes actualizar órdenes asignadas a ti");
            }
        }

        if (cierreDto.getDiagnostico() != null) servicio.setDiagnostico(cierreDto.getDiagnostico());
        if (cierreDto.getSolucion() != null) servicio.setSolucion(cierreDto.getSolucion());
        if (cierreDto.getPartesCambiadas() != null) servicio.setPartesCambiadas(cierreDto.getPartesCambiadas());
        if (cierreDto.getGarantiaServicio() != null) servicio.setGarantiaServicio(cierreDto.getGarantiaServicio());
        if (cierreDto.getCostoServicio() != null) {
            servicio.setCostoServicio(cierreDto.getCostoServicio());
        }
        if (cierreDto.getCostoRepuestos() != null) {
            servicio.setCostoRepuestos(cierreDto.getCostoRepuestos());
        }
        if (cierreDto.getObservaciones() != null) servicio.setObservaciones(cierreDto.getObservaciones());

        String estadoAnterior = estadoVisualDesdeCodigo(servicio.getEstado());
        String estadoNormalizado = normalizarEstado(nuevoEstado);
        TipoEvento tipoEvento = resolverTipoEventoEstado(estadoNormalizado);

        servicio.setEstado(tipoEvento.getId());

        if (("LISTO".equalsIgnoreCase(estadoNormalizado) || "REPARADO".equalsIgnoreCase(estadoNormalizado))
                && servicio.getGarantiaServicio() != null) {
            servicio.setVencimientoGarantia(LocalDate.now().plusDays(servicio.getGarantiaServicio()));
        }

        OrdenDeServicio actualizado = servicioRepository.save(servicio);

        registrarAuditoriaEstado(actualizado, estadoAnterior, estadoNormalizado, tipoEvento.getId(), usernameLogeado);

        return convertirADto(actualizado);
    }

    /**
     * Marca una orden como ENTREGADA. Accesible para ADMIN y TECNICO.
     */
    public OrdenDeServicioDto entregarOrden(String id, String usernameLogeado) {
        return cambiarEstado(id, "ENTREGADO", usernameLogeado);
    }

    public List<OrdenDeServicioDto> obtenerGarantiasPorVencer(LocalDate desde, LocalDate hasta) {
        return servicioRepository.findGarantiasPorVencer(desde, hasta).stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad a DTO resolviendo el nombre del estado desde el mapa precargado.
     * Cuando mapaEstados es null, usa estadoVisualDesdeCodigo (consulta individual a BD).
     */
    private OrdenDeServicioDto convertirADto(OrdenDeServicio servicio, Map<String, String> mapaEstados) {
        OrdenDeServicioDto dto = new OrdenDeServicioDto();
        dto.setId(servicio.getId());
        dto.setClienteId(servicio.getCliente() != null ? servicio.getCliente().getId() : null);
        dto.setClienteTipoDocumentoId(servicio.getCliente() != null ? servicio.getCliente().getTipoDocumentoId() : null);
        dto.setClienteNombre(servicio.getCliente() != null ? servicio.getCliente().getNombre() : null);
        dto.setClienteApellido(servicio.getCliente() != null ? servicio.getCliente().getApellido() : null);
        dto.setClienteTelefono(servicio.getCliente() != null ? servicio.getCliente().getTelefono() : null);
        dto.setClienteEmail(servicio.getCliente() != null ? servicio.getCliente().getEmail() : null);
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
        // Resolver tipo_evento.nombre: usar mapa precargado si está disponible
        String codigoEstado = servicio.getEstado();
        String nombreEstado = (mapaEstados != null && codigoEstado != null && mapaEstados.containsKey(codigoEstado))
                ? mapaEstados.get(codigoEstado)
                : estadoVisualDesdeCodigo(codigoEstado);
        dto.setEstado(nombreEstado);
        dto.setFechaIngreso(servicio.getFechaIngreso());
        dto.setFechaSalida(servicio.getFechaSalida());
        dto.setGarantiaServicio(servicio.getGarantiaServicio());
        dto.setVencimientoGarantia(servicio.getVencimientoGarantia());
        dto.setUsuarioUsername(servicio.getUsuario() != null ? servicio.getUsuario().getUsername() : null);
        dto.setUsuarioNombre(servicio.getUsuario() != null ? servicio.getUsuario().getFirstName() + " " + servicio.getUsuario().getLastName() : null);
        dto.setTecnicoAsignadoUsername(servicio.getTecnicoAsignado() != null ? servicio.getTecnicoAsignado().getUsername() : null);
        dto.setTecnicoAsignadoNombre(servicio.getTecnicoAsignado() != null ? servicio.getTecnicoAsignado().getFirstName() + " " + servicio.getTecnicoAsignado().getLastName() : null);
        dto.setFechaAsignacion(servicio.getFechaAsignacion());
        dto.setCodigoEstado(codigoEstado); // código interno: SOC, SOA, etc.
        dto.setObservaciones(servicio.getObservaciones());
        return dto;
    }

    /** Sobrecarga para operaciones de registro único (usa estadoVisualDesdeCodigo individual). */
    private OrdenDeServicioDto convertirADto(OrdenDeServicio servicio) {
        return convertirADto(servicio, null);
    }

    /**
     * Carga todos los TipoEvento en una sola consulta y devuelve el mapa {id → nombre}.
     * Úsalo antes de convertir listas de órdenes para evitar el problema N+1.
     */
    private Map<String, String> buildMapaEstados() {
        Map<String, String> mapa = new HashMap<>();
        tipoEventoRepository.findAll().forEach(te -> {
            if (te.getId() != null && te.getNombre() != null) {
                mapa.put(te.getId(), te.getNombre());
            }
        });
        return mapa;
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
            // Alias de nombre completo + alias corto
            case "ORDEN_SERVICIO_CREADA":
            case "RECIBIDO":
                return "ORDEN_SERVICIO_CREADA";
            case "ORDEN_SERVICIO_ASIGNADA":
            case "ASIGNADO":
                return "ORDEN_SERVICIO_ASIGNADA";
            case "ORDEN_SERVICIO_EN_PROCESO":
            case "EN_PROCESO":
                return "ORDEN_SERVICIO_EN_PROCESO";
            case "ORDEN_SERVICIO_DIAGNOSTICADA":
            case "EN_DIAGNOSTICO":
                return "ORDEN_SERVICIO_DIAGNOSTICADA";
            case "ORDEN_SERVICIO_REPARADA":
            case "REPARADO":
                return "ORDEN_SERVICIO_REPARADA";
            case "ORDEN_SERVICIO_LISTA":
            case "LISTO":
                return "ORDEN_SERVICIO_LISTA";
            case "ORDEN_SERVICIO_ENTREGADA":
            case "ENTREGADO":
                return "ORDEN_SERVICIO_ENTREGADA";
            case "ORDEN_SERVICIO_CANCELADA":
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

    /**
     * Devuelve el nombre del TipoEvento (e.g., "ORDEN_SERVICIO_CREADA") a partir del código
     * almacenado en orden_de_servicio.estado. Intenta dos búsquedas para ser robusto:
     * 1) por id  (caso normal: el campo almacena el TipoEvento.id)
     * 2) por nombre (fallback: datos legados donde se guardó el nombre directamente)
     */
    private String estadoVisualDesdeCodigo(String codigoEstado) {
        if (codigoEstado == null || codigoEstado.isBlank()) {
            return "-";
        }
        // Búsqueda principal: por TipoEvento.id
        TipoEvento tipoEvento = tipoEventoRepository.findById(codigoEstado).orElse(null);
        if (tipoEvento != null && tipoEvento.getNombre() != null) {
            return tipoEvento.getNombre();
        }
        // Fallback: el campo podría contener el nombre directamente (datos migrados / legados)
        TipoEvento porNombre = tipoEventoRepository.findByNombre(codigoEstado);
        if (porNombre != null) {
            return porNombre.getNombre();
        }
        // Último recurso: devolver el valor crudo
        return codigoEstado;
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
