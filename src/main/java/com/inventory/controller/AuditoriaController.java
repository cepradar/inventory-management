package com.inventory.controller;

import com.inventory.dto.AuditoriaDto;
import com.inventory.service.AuditoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Obtiene todos los movimientos de productos
     */
    @GetMapping("/movimientos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDto>> obtenerTodosMovimientos() {
        List<AuditoriaDto> movimientos = auditoriaService.obtenerTodosMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos de un producto específico
     */
    @GetMapping("/producto/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDto>> obtenerMovimientosProducto(@PathVariable String productId) {
        List<AuditoriaDto> movimientos = auditoriaService.obtenerMovimientosProducto(productId);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos realizados por un usuario
     */
    @GetMapping("/usuario/{usuarioUsername}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDto>> obtenerMovimientosUsuario(@PathVariable String usuarioUsername) {
        List<AuditoriaDto> movimientos = auditoriaService.obtenerMovimientosUsuario(usuarioUsername);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos por tipo (INGRESO o SALIDA)
     */
    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDto>> obtenerMovimientosPorTipo(@PathVariable String tipo) {
        List<AuditoriaDto> movimientos = auditoriaService.obtenerMovimientosPorTipo(tipo);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos en un rango de fechas
     */
    @GetMapping("/rango")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDto>> obtenerMovimientosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<AuditoriaDto> movimientos = auditoriaService.obtenerMovimientosEnRango(fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene un movimiento por ID
     */
    @GetMapping("/{movimientoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditoriaDto> obtenerMovimientoPorId(@PathVariable Long movimientoId) {
        AuditoriaDto movimiento = auditoriaService.obtenerMovimientoPorId(movimientoId);
        return ResponseEntity.ok(movimiento);
    }

    /**
     * Obtiene eventos por categoría (INVENTARIO, VENTAS, SERVICIOS, etc.)
     */
    @GetMapping("/categoria/{categoria}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditoriaDto>> obtenerPorCategoria(@PathVariable String categoria) {
        List<AuditoriaDto> movimientos = auditoriaService.obtenerMovimientosEnCategoria(categoria);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Registra un nuevo movimiento de producto (uso interno)
     * Permite a usuarios autenticados registrar sus propias acciones
     */
    @PostMapping("/registrar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuditoriaDto> registrarMovimiento(
            @RequestParam String productId,
            @RequestParam Integer cantidad,
            @RequestParam String tipo,
            @RequestParam String descripcion,
            @RequestParam String usuarioUsername,
            @RequestParam(required = false) String referencia) {
        AuditoriaDto movimiento = auditoriaService.registrarMovimiento(
                productId, cantidad, tipo, descripcion, usuarioUsername, referencia);
        return ResponseEntity.ok(movimiento);
    }
}
