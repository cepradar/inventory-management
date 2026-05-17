package com.inventory.controller;

import com.inventory.dto.VentaDto;
import com.inventory.dto.VentaRegistroDto;
import com.inventory.service.VentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ventas")
@PreAuthorize("hasRole('ADMIN')")
public class VentasController {

    @Autowired
    private VentasService ventasService;

    /**
     * Registra una nueva venta. ADMIN puede registrar cualquier venta;
     * TECNICO sólo puede registrar ventas sobre órdenes que tiene asignadas.
     */
    @PostMapping("/registrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<VentaDto> registrarVenta(@RequestBody VentaRegistroDto registroDto, Authentication auth) {
        // Username siempre se toma del JWT — nunca del cuerpo de la petición
        registroDto.setUsuarioUsername(auth.getName());
        boolean isTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));
        if (isTecnico && registroDto.getOrdenDeServicioId() != null) {
            ventasService.validarAccesoOrdenParaVenta(registroDto.getOrdenDeServicioId(), auth.getName());
        }
        VentaDto venta = ventasService.registrarVenta(registroDto);
        return ResponseEntity.ok(venta);
    }

    /**
     * Lista ventas. ADMIN ve todas; TECNICO solo ve las ventas que registró él.
     */
    @GetMapping("/listar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<List<VentaDto>> obtenerTodasVentas(Authentication auth) {
        boolean isTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));
        List<VentaDto> ventas = isTecnico
                ? ventasService.obtenerVentasUsuario(auth.getName())
                : ventasService.obtenerTodasVentas();
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene ventas de un producto específico
     */
    @GetMapping("/producto/{productId}")
    public ResponseEntity<List<VentaDto>> obtenerVentasProducto(@PathVariable String productId) {
        List<VentaDto> ventas = ventasService.obtenerVentasProducto(productId);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Ventas por usuario. TECNICO solo puede consultar sus propias ventas.
     */
    @GetMapping("/usuario/{usuarioUsername}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<List<VentaDto>> obtenerVentasUsuario(@PathVariable String usuarioUsername,
            Authentication auth) {
        boolean isTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));
        // TECNICO solo puede consultar sus propias ventas
        String usernameEfectivo = isTecnico ? auth.getName() : usuarioUsername;
        List<VentaDto> ventas = ventasService.obtenerVentasUsuario(usernameEfectivo);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene ventas en un rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<List<VentaDto>> obtenerVentasEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<VentaDto> ventas = ventasService.obtenerVentasEnRango(fechaInicio, fechaFin);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene ventas por nombre de comprador
     */
    @GetMapping("/comprador/{nombreComprador}")
    public ResponseEntity<List<VentaDto>> obtenerVentasPorComprador(@PathVariable String nombreComprador) {
        List<VentaDto> ventas = ventasService.obtenerVentasPorComprador(nombreComprador);
        return ResponseEntity.ok(ventas);
    }

    /**
     * Obtiene una venta por ID. TECNICO solo puede ver ventas propias.
     */
    @GetMapping("/{ventaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<VentaDto> obtenerVentaPorId(@PathVariable Long ventaId, Authentication auth) {
        VentaDto venta = ventasService.obtenerVentaPorId(ventaId);
        boolean isTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));
        if (isTecnico && !venta.getUsuarioUsername().equals(auth.getName())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No tienes permiso para ver esta venta");
        }
        return ResponseEntity.ok(venta);
    }

    /**
     * Obtiene el total de ventas en un rango de fechas
     */
    @GetMapping("/total/rango")
    public ResponseEntity<Map<String, BigDecimal>> obtenerTotalVentasEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        BigDecimal total = ventasService.obtenerTotalVentasEnRango(fechaInicio, fechaFin);
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    /**
     * Ventas asociadas a una orden de servicio.
     * 
     * REGLAS:
     * - ADMIN puede consultar cualquier orden
     * - TECNICO solo puede consultar órdenes asignadas a él
     */
    @GetMapping("/orden/{ordenId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<List<VentaDto>> obtenerVentasPorOrden(
            @PathVariable String ordenId,
            Authentication auth) {

        boolean isTecnico = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECNICO"));

        // Validar acceso únicamente para técnicos
        if (isTecnico) {
            ventasService.validarAccesoOrdenParaVenta(ordenId, auth.getName());
        }

        List<VentaDto> ventas = ventasService.obtenerVentasPorOrden(ordenId);

        return ResponseEntity.ok(ventas);
    }
}
