-- Migración: Añadir columnas de módulo, tipo documento, código y versión a report_templates
-- Ejecutar una sola vez en producción (Hibernate ddl-auto=update lo hace automático en dev)

ALTER TABLE report_templates
    ADD COLUMN IF NOT EXISTS codigo        VARCHAR(100) UNIQUE,
    ADD COLUMN IF NOT EXISTS modulo        VARCHAR(50),
    ADD COLUMN IF NOT EXISTS tipo_documento VARCHAR(60),
    ADD COLUMN IF NOT EXISTS version       VARCHAR(20) DEFAULT '1.0';

-- Índice para búsqueda rápida por módulo + tipo de documento
CREATE INDEX IF NOT EXISTS idx_report_templates_modulo_tipo
    ON report_templates (modulo, tipo_documento)
    WHERE activo = TRUE;

-- Crear índice por código único
CREATE INDEX IF NOT EXISTS idx_report_templates_codigo
    ON report_templates (codigo)
    WHERE activo = TRUE;

-- Actualizar registros existentes con valores por defecto basados en tipo_reporte
UPDATE report_templates SET modulo = 'VENTAS',  tipo_documento = 'FACTURA_VENTA',  version = '1.0' WHERE tipo_reporte = 'FACTURA'          AND modulo IS NULL;
UPDATE report_templates SET modulo = 'ORDENES', tipo_documento = 'ORDEN_SERVICIO', version = '1.0' WHERE tipo_reporte = 'ORDEN_SERVICIO'   AND modulo IS NULL;
UPDATE report_templates SET modulo = 'GENERAL', tipo_documento = 'GENERICO',       version = '1.0' WHERE modulo IS NULL;
