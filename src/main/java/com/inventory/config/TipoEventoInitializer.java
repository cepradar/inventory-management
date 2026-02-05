package com.inventory.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.inventory.model.CategoriaTipoEvento;
import com.inventory.model.TipoEvento;
import com.inventory.repository.CategoriaTipoEventoRepository;
import com.inventory.repository.TipoEventoRepository;

@Configuration
public class TipoEventoInitializer {
    private static final Logger logger = LoggerFactory.getLogger(TipoEventoInitializer.class);

    @Bean
    CommandLineRunner initTipoEventos(TipoEventoRepository eventoRepository, CategoriaTipoEventoRepository categoriaRepository) {
        return args -> {
            logger.info("🚀 Inicializando categorías y tipos de eventos...");

            CategoriaTipoEvento inventario = crearCategoriaSiNoExiste(categoriaRepository, "INVENTARIO");
            CategoriaTipoEvento orden = crearCategoriaSiNoExiste(categoriaRepository, "ORDEN");
            CategoriaTipoEvento venta = crearCategoriaSiNoExiste(categoriaRepository, "VENTA");
            
            // ========== EVENTOS DE COMPRA Y VENTA ==========
            crearEventoSiNoExiste(eventoRepository, "C", "COMPRA", venta);
            crearEventoSiNoExiste(eventoRepository, "V", "VENTA", venta);

            // ========== EVENTOS DE MOVIMIENTO DE INVENTARIO ==========
            crearEventoSiNoExiste(eventoRepository, "ME", "MOVIMIENTO_ENTRADA", inventario);
            crearEventoSiNoExiste(eventoRepository, "MS", "MOVIMIENTO_SALIDA", inventario);
            crearEventoSiNoExiste(eventoRepository, "MA", "MOVIMIENTO_AJUSTE", inventario);
            crearEventoSiNoExiste(eventoRepository, "MT", "MOVIMIENTO_TRANSFERENCIA", inventario);
            crearEventoSiNoExiste(eventoRepository, "MD", "MOVIMIENTO_DEVOLUCION", inventario);
            crearEventoSiNoExiste(eventoRepository, "MI", "MOVIMIENTO_INVENTARIO_INICIAL", inventario);
            crearEventoSiNoExiste(eventoRepository, "CP", "CREACION_PRODUCTO", inventario);
            crearEventoSiNoExiste(eventoRepository, "EP", "ELIMINACION_PRODUCTO", inventario);

            // ========== EVENTOS DE PROCESO DE VENTA ==========
            crearEventoSiNoExiste(eventoRepository, "VC", "VENTA_CREADA", venta);
            crearEventoSiNoExiste(eventoRepository, "VP", "VENTA_PAGADA", venta);
            crearEventoSiNoExiste(eventoRepository, "VE", "VENTA_ENTREGADA", venta);
            crearEventoSiNoExiste(eventoRepository, "VAN", "VENTA_ANULADA", venta);
            crearEventoSiNoExiste(eventoRepository, "VDE", "VENTA_DEVUELTA", venta);

            // ========== EVENTOS DE ÓRDENES DE SERVICIO ==========
            crearEventoSiNoExiste(eventoRepository, "SOC", "ORDEN_SERVICIO_CREADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOA", "ORDEN_SERVICIO_ASIGNADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOE", "ORDEN_SERVICIO_EN_PROCESO", orden);
            crearEventoSiNoExiste(eventoRepository, "SOP", "ORDEN_SERVICIO_PAUSADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOD", "ORDEN_SERVICIO_DIAGNOSTICADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOR", "ORDEN_SERVICIO_REPARADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOT", "ORDEN_SERVICIO_PRUEBA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOL", "ORDEN_SERVICIO_LISTA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOENT", "ORDEN_SERVICIO_ENTREGADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOCAN", "ORDEN_SERVICIO_CANCELADA", orden);
            crearEventoSiNoExiste(eventoRepository, "SOREC", "ORDEN_SERVICIO_RECHAZADA", orden);

            // ========== EVENTOS DE GARANTÍA ==========
            crearEventoSiNoExiste(eventoRepository, "GACTIVA", "GARANTIA_ACTIVADA", orden);
            crearEventoSiNoExiste(eventoRepository, "GVENCIDA", "GARANTIA_VENCIDA", orden);
            crearEventoSiNoExiste(eventoRepository, "GRECLAMO", "GARANTIA_RECLAMO", orden);

            // ========== EVENTOS DE AUDITORÍA Y SISTEMA ==========
            crearEventoSiNoExiste(eventoRepository, "AU", "AUDITORIA", orden);
            crearEventoSiNoExiste(eventoRepository, "SIS", "SISTEMA", orden);
            
            logger.info("✅ Inicialización de categorías y tipos de eventos completada");
        };
    }

    private CategoriaTipoEvento crearCategoriaSiNoExiste(CategoriaTipoEventoRepository categoriaRepository, String nombre) {
        return categoriaRepository.findByNombre(nombre)
            .orElseGet(() -> categoriaRepository.save(new CategoriaTipoEvento(nombre)));
    }

    /**
     * Método auxiliar para crear eventos de tipo únicamente si no existen
     * @param eventoRepository repositorio de eventos
     * @param codigo código corto del evento
     * @param nombre nombre descriptivo del evento
     * @param categoria categoría del evento (INVENTARIO, ORDEN, VENTA)
     */
    private void crearEventoSiNoExiste(TipoEventoRepository eventoRepository, String codigo, String nombre, CategoriaTipoEvento categoria) {
        try {
            // Buscar por código (ID)
            TipoEvento evento = eventoRepository.findById(codigo).orElse(null);
            if (evento == null) {
                TipoEvento nuevoEvento = new TipoEvento(codigo, nombre, categoria);
                eventoRepository.save(nuevoEvento);
                logger.debug("✅ Tipo de evento creado: {} - {} [{}]", codigo, nombre, categoria.getNombre());
            } else {
                if (evento.getCategoria() == null) {
                    evento.setCategoria(categoria);
                    eventoRepository.save(evento);
                    logger.debug("🛠️ Tipo de evento actualizado: {} - {} [{}]", codigo, nombre, categoria.getNombre());
                } else {
                    logger.debug("ℹ️ Tipo de evento ya existe: {} - {} [{}]", codigo, nombre, categoria.getNombre());
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error al crear tipo de evento {} - {}: {}", codigo, nombre, e.getMessage());
        }
    }
}
