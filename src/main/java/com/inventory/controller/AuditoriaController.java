package com.inventory.controller;

import com.inventory.dto.MovimientoProductoDto;
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
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    @Autowired
    private AuditoriaService auditoriaService;

    /**
     * Obtiene todos los movimientos de productos
     */
    @GetMapping("/movimientos")
    public ResponseEntity<List<MovimientoProductoDto>> obtenerTodosMovimientos() {
        List<MovimientoProductoDto> movimientos = auditoriaService.obtenerTodosMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos de un producto específico
     */
    @GetMapping("/producto/{productId}")
    public ResponseEntity<List<MovimientoProductoDto>> obtenerMovimientosProducto(@PathVariable String productId) {
        List<MovimientoProductoDto> movimientos = auditoriaService.obtenerMovimientosProducto(productId);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos realizados por un usuario
     */
    @GetMapping("/usuario/{usuarioUsername}")
    public ResponseEntity<List<MovimientoProductoDto>> obtenerMovimientosUsuario(@PathVariable String usuarioUsername) {
        List<MovimientoProductoDto> movimientos = auditoriaService.obtenerMovimientosUsuario(usuarioUsername);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos por tipo (INGRESO o SALIDA)
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientoProductoDto>> obtenerMovimientosPorTipo(@PathVariable String tipo) {
        List<MovimientoProductoDto> movimientos = auditoriaService.obtenerMovimientosPorTipo(tipo);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene movimientos en un rango de fechas
     */
    @GetMapping("/rango")
    public ResponseEntity<List<MovimientoProductoDto>> obtenerMovimientosEnRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<MovimientoProductoDto> movimientos = auditoriaService.obtenerMovimientosEnRango(fechaInicio, fechaFin);
        return ResponseEntity.ok(movimientos);
    }

    /**
     * Obtiene un movimiento por ID
     */
    @GetMapping("/{movimientoId}")
    public ResponseEntity<MovimientoProductoDto> obtenerMovimientoPorId(@PathVariable Long movimientoId) {
        MovimientoProductoDto movimiento = auditoriaService.obtenerMovimientoPorId(movimientoId);
        return ResponseEntity.ok(movimiento);
    }

    /**
     * Registra un nuevo movimiento de producto (uso interno)
     */
    @PostMapping("/registrar")
    public ResponseEntity<MovimientoProductoDto> registrarMovimiento(
            @RequestParam String productId,
            @RequestParam Integer cantidad,
            @RequestParam String tipo,
            @RequestParam String descripcion,
            @RequestParam String usuarioUsername,
            @RequestParam(required = false) String referencia) {
        MovimientoProductoDto movimiento = auditoriaService.registrarMovimiento(
                productId, cantidad, tipo, descripcion, usuarioUsername, referencia);
        return ResponseEntity.ok(movimiento);
    }
}
