package com.inventory.controller;

import com.inventory.dto.OrdenDeServicioDto;
import com.inventory.service.DocumentoGeneradorService;
import com.inventory.service.OrdenDeServicioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios-reparacion")
public class OrdenDeServicioController {

    private static final Logger log = LoggerFactory.getLogger(OrdenDeServicioController.class);

    @Autowired
    private OrdenDeServicioService service;

    @Autowired
    private DocumentoGeneradorService documentoGeneradorService;

    @PostMapping("/registrar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registrar(@RequestBody OrdenDeServicioDto dto, Authentication auth) {
        try {
            log.info("Recibiendo solicitud de registro de servicio: {}", dto);
            OrdenDeServicioDto created = service.registrarServicio(dto, auth.getName());
            log.info("Servicio registrado exitosamente con ID: {}", created.getId());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Error al registrar servicio: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> actualizar(@PathVariable String id, @RequestBody OrdenDeServicioDto dto) {
        try {
            OrdenDeServicioDto updated = service.actualizarServicio(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            OrdenDeServicioDto dto = service.obtenerServicioPorId(id);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> porCliente(@PathVariable String clienteId) {
        try {
            List<OrdenDeServicioDto> list = service.obtenerServiciosPorCliente(clienteId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}/{tipoDocumentoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> porCliente(@PathVariable String clienteId, @PathVariable String tipoDocumentoId) {
        try {
            List<OrdenDeServicioDto> list = service.obtenerServiciosPorCliente(clienteId, tipoDocumentoId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar() {
        try {
            List<OrdenDeServicioDto> list = service.obtenerTodosServicios();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Regla 2: devuelve SOLO las órdenes en estado ORDEN_SERVICIO_CREADA (SOC).
     * Usada por la pantalla "Asignar Técnico".
     */
    @GetMapping("/pendientes-asignacion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> pendientesAsignacion() {
        try {
            List<OrdenDeServicioDto> list = service.obtenerOrdenesParaAsignar();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Regla 3: asigna un técnico a la orden, cambia estado a SOA y registra auditoría.
     * Body: { "tecnicoUsername": "username_del_tecnico" }
     */
    @PostMapping("/{id}/asignar-tecnico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> asignarTecnico(@PathVariable String id,
                                             @RequestBody Map<String, String> body,
                                             Authentication auth) {
        try {
            String tecnicoUsername = body.get("tecnicoUsername");
            if (tecnicoUsername == null || tecnicoUsername.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El campo 'tecnicoUsername' es obligatorio");
            }
            OrdenDeServicioDto updated = service.asignarTecnico(id, tecnicoUsername.trim(), auth.getName());
            log.info("Técnico {} asignado a orden {} por {}", tecnicoUsername, id, auth.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error al asignar técnico a orden {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Regla 4: devuelve SOLO las órdenes asignadas al técnico autenticado.
     * Usada por la pantalla "Responder Orden".
     */
    @GetMapping("/mis-ordenes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> misOrdenes(Authentication auth) {
        try {
            List<OrdenDeServicioDto> list = service.obtenerMisOrdenes(auth.getName());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Devuelve órdenes en estado LISTA o REPARADA, listas para entregar.
     */
    @GetMapping("/ordenes-para-entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<?> obtenerOrdenesParaEntregar() {
        try {
            List<OrdenDeServicioDto> list = service.obtenerOrdenesParaEntregar();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    /**
     * Permite a un técnico (o admin) actualizar los campos técnicos de la orden
     * y cambiar su estado (EN_PROCESO, REPARADO, etc.) en una sola llamada.
     * Los técnicos solo pueden actualizar órdenes asignadas a ellos.
     */
    @PutMapping("/{id}/cerrar-tecnico")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<?> cerrarOrdenTecnico(@PathVariable String id,
                                                @RequestBody OrdenDeServicioDto body,
                                                Authentication auth) {
        try {
            String nuevoEstado = body.getEstado();
            if (nuevoEstado == null || nuevoEstado.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'estado' es obligatorio");
            }
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            OrdenDeServicioDto updated = service.cerrarOrdenPorTecnico(id, body, nuevoEstado, auth.getName(), isAdmin);
            log.info("Orden {} actualizada a estado {} por {}", id, nuevoEstado, auth.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error al cerrar orden técnica {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Marca una orden como ENTREGADA. Accesible para ADMIN y TECNICO.
     */
    @PutMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<?> entregarOrden(@PathVariable String id, Authentication auth) {
        try {
            OrdenDeServicioDto updated = service.entregarOrden(id, auth.getName());
            log.info("Orden {} marcada como ENTREGADA por {}", id, auth.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error al entregar orden {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cambiarEstado(@PathVariable String id, @PathVariable String estado, Authentication auth) {
        try {
            OrdenDeServicioDto updated = service.cambiarEstado(id, estado, auth.getName());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            service.eliminarServicio(id);
            return ResponseEntity.ok("Eliminado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    /**
     * Genera el PDF de la orden de servicio y lo devuelve como descarga.
     * El PDF se construye en memoria y no se persiste en disco.
     * ADMIN puede descargar cualquier orden; TECNICO solo las asignadas a él.
     */
    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable String id, Authentication auth) {
        try {
            // Validar acceso para técnicos: solo pueden descargar su propia orden
            boolean isTecnico = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));
            if (isTecnico) {
                OrdenDeServicioDto orden = service.obtenerServicioPorId(id);
                if (orden.getTecnicoAsignadoUsername() == null
                        || !orden.getTecnicoAsignadoUsername().equals(auth.getName())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            }

            byte[] pdfBytes = documentoGeneradorService.generarOrdenServicio(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "orden-servicio-" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            log.info("PDF generado para orden {} por {}", id, auth.getName());
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al generar PDF para orden {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
