package com.inventory.service;

import com.inventory.dto.AuditoriaDto;
import com.inventory.model.Auditoria;
import com.inventory.model.Product;
import com.inventory.model.TipoEvento;
import com.inventory.model.User;
import com.inventory.repository.AuditoriaRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.TipoEventoRepository;
import com.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditoriaService {
    private static final Logger logger = LoggerFactory.getLogger(AuditoriaService.class);

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    /**
     * Registra un nuevo evento de auditoría
     * Mapea tipos desde frontend a IDs de tipo_evento:
     * - INGRESO → ME (MOVIMIENTO_ENTRADA)
     * - AJUSTE → MA (MOVIMIENTO_AJUSTE)
     * - SALIDA → MS (MOVIMIENTO_SALIDA)
     */
    public AuditoriaDto registrarMovimiento(String productId, Integer cantidadInicial, Integer cantidadFinal,
                                            BigDecimal precioInicial, BigDecimal precioFinal, String tipo,
                                            String descripcion, String usuarioUsername, String referencia) {
        // Mapear tipo del frontend a ID de tipo_evento
        String tipoEventoId = mapearTipoEvento(tipo);
        logger.info("🔍 Registrando auditoría - tipo entrada: '{}', tipo mapeado: '{}'", tipo, tipoEventoId);
        
        // Buscar tipo de evento
        TipoEvento tipoEvento = tipoEventoRepository.findById(tipoEventoId)
                .orElseThrow(() -> {
                    logger.error("❌ Tipo de evento NO ENCONTRADO en BD: '{}'", tipoEventoId);
                    return new RuntimeException("Tipo de evento no encontrado: " + tipoEventoId);
                });
        
        logger.info("✅ Tipo de evento encontrado: {} - {}", tipoEvento.getId(), tipoEvento.getNombre());
        
        // Buscar producto para capturar nombre (sin FK)
        Product producto = productRepository.findById(productId).orElse(null);
        String productName = producto != null ? producto.getName() : "[Producto eliminado]";
        
        User usuario = userRepository.findByUsernameIgnoreCase(usuarioUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Integer cantidadInicialFinal = cantidadInicial != null ? cantidadInicial : cantidadFinal;
        Integer cantidadFinalFinal = cantidadFinal != null ? cantidadFinal : cantidadInicialFinal;
        BigDecimal precioInicialFinal = precioInicial != null ? precioInicial : BigDecimal.ZERO;
        BigDecimal precioFinalFinal = precioFinal != null ? precioFinal : precioInicialFinal;

        Auditoria auditoria = new Auditoria(
            tipoEvento,
            productId,
            productName,
            cantidadInicialFinal,
            cantidadFinalFinal,
            precioInicialFinal,
            precioFinalFinal,
            descripcion,
            usuario,
            referencia
        );
        Auditoria guardada = auditoriaRepository.save(auditoria);
        
        logger.info("✅ Auditoría registrada con ID: {}, tipoEvento: {}", guardada.getId(), 
                   guardada.getTipoEvento() != null ? guardada.getTipoEvento().getId() : "NULL");
        
        return new AuditoriaDto(guardada);
    }

    /**
     * Mapea los tipos de eventos del frontend a los códigos de la tabla tipo_evento
     */
    private String mapearTipoEvento(String tipo) {
        if (tipo == null || tipo.trim().isEmpty()) {
            logger.warn("⚠️ Tipo de evento vacío, usando MA por defecto");
            return "MA";
        }
        
        String tipoUpper = tipo.toUpperCase().trim();
        
        switch(tipoUpper) {
            // Nuevos códigos desde frontend (PRIORITARIOS)
            case "CP":
                return "CP"; // CREACION_PRODUCTO
            case "MA":
                return "MA"; // MOVIMIENTO_AJUSTE
            case "EP":
                return "EP"; // ELIMINACION_PRODUCTO
            // Códigos legados (compatibilidad hacia atrás)
            case "INGRESO":
                logger.info("ℹ️ Usando código legacy 'INGRESO', mapeando a 'ME'");
                return "ME"; // MOVIMIENTO_ENTRADA
            case "SALIDA":
                logger.info("ℹ️ Usando código legacy 'SALIDA', mapeando a 'MS'");
                return "MS"; // MOVIMIENTO_SALIDA
            case "AJUSTE":
                logger.info("ℹ️ Usando código legacy 'AJUSTE', mapeando a 'MA'");
                return "MA"; // MOVIMIENTO_AJUSTE
            case "ME":
            case "MS":
                // Ya son códigos válidos, pasarlos directamente
                return tipoUpper;
            default:
                logger.warn("⚠️ Código de evento desconocido: '{}', usando MA por defecto", tipo);
                return "MA"; // Fallback a MOVIMIENTO_AJUSTE
        }
    }

    /**
     * Obtiene todos los eventos de auditoría ordenados por fecha descendente
     */
    public List<AuditoriaDto> obtenerTodosMovimientos() {
        logger.info("🔍 Obteniendo todos los movimientos de auditoría...");
        List<Auditoria> auditorias = auditoriaRepository.findAllByOrderByFechaDesc();
        logger.info("📊 Total de registros encontrados en BD: {}", auditorias.size());
        
        List<AuditoriaDto> dtos = auditorias.stream()
                .map(auditoria -> {
                    try {
                        return new AuditoriaDto(auditoria);
                    } catch (Exception e) {
                        logger.error("❌ Error al convertir auditoría {} a DTO: {}", 
                                   auditoria.getId(), e.getMessage(), e);
                        return null;
                    }
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
        
        logger.info("✅ DTOs creados exitosamente: {}", dtos.size());
        return dtos;
    }

    /**
     * Obtiene eventos de un producto específico
     */
    public List<AuditoriaDto> obtenerMovimientosProducto(String productId) {
        return auditoriaRepository.findByProductId(productId).stream()
                .map(AuditoriaDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene eventos de un usuario
     */
    public List<AuditoriaDto> obtenerMovimientosUsuario(String username) {
        return auditoriaRepository.findByUsuarioUsernameIgnoreCase(username).stream()
                .map(AuditoriaDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene eventos por tipo de evento específico
     */
    public List<AuditoriaDto> obtenerMovimientosPorTipo(String tipoEventoId) {
        return auditoriaRepository.findByTipoEventoId(tipoEventoId).stream()
                .map(AuditoriaDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene eventos en un rango de fechas
     */
    public List<AuditoriaDto> obtenerMovimientosEnRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return auditoriaRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(AuditoriaDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene eventos por categoría (INVENTARIO, VENTA, SERVICIO, GARANTIA, SISTEMA)
     */
    public List<AuditoriaDto> obtenerMovimientosEnCategoria(String categoria) {
        return auditoriaRepository.findByTipoEventoCategoria(categoria.toUpperCase()).stream()
                .map(AuditoriaDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un evento por ID
     */
    public AuditoriaDto obtenerMovimientoPorId(Long auditoriaId) {
        return auditoriaRepository.findById(auditoriaId)
                .map(AuditoriaDto::new)
                .orElseThrow(() -> new RuntimeException("Evento de auditoría no encontrado"));
    }
}
