package com.inventory.controller;

import com.inventory.dto.VentaDto;
import com.inventory.service.VentasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Registra una nueva venta
     */
    @PostMapping("/registrar")
    public ResponseEntity<VentaDto> registrarVenta(
            @RequestParam String productId,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal precioUnitario,
            @RequestParam String nombreComprador,
            @RequestParam(required = false) String telefonoComprador,
            @RequestParam(required = false) String emailComprador,
            @RequestParam String usuarioUsername,
            @RequestParam(required = false) String observaciones) {
        VentaDto venta = ventasService.registrarVenta(
                productId, cantidad, precioUnitario, nombreComprador,
                telefonoComprador, emailComprador, usuarioUsername, observaciones);
        return ResponseEntity.ok(venta);
    }

    /**
     * Obtiene todas las ventas
     */
    @GetMapping("/listar")
    public ResponseEntity<List<VentaDto>> obtenerTodasVentas() {
        List<VentaDto> ventas = ventasService.obtenerTodasVentas();
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
     * Obtiene ventas realizadas por un usuario
     */
    @GetMapping("/usuario/{usuarioUsername}")
    public ResponseEntity<List<VentaDto>> obtenerVentasUsuario(@PathVariable String usuarioUsername) {
        List<VentaDto> ventas = ventasService.obtenerVentasUsuario(usuarioUsername);
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
     * Obtiene una venta por ID
     */
    @GetMapping("/{ventaId}")
    public ResponseEntity<VentaDto> obtenerVentaPorId(@PathVariable Long ventaId) {
        VentaDto venta = ventasService.obtenerVentaPorId(ventaId);
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
}
