-- Script para eliminar tablas movimiento_* de la base de datos

-- Primero verificar si existen y luego eliminar
DROP TABLE IF EXISTS movimiento_producto CASCADE;
DROP TABLE IF EXISTS movimiento_inventario CASCADE;
DROP TABLE IF EXISTS movimientos_inventario CASCADE;

-- Mensaje de confirmación (comentario)
-- Tablas movimiento_* eliminadas exitosamente
