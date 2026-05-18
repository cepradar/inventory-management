-- ================================================================
-- Migración: Venta → FK compuesta hacia clientes (id + tipo_documento)
-- Ejecutar UNA VEZ antes de reiniciar la aplicación con el código nuevo.
-- ================================================================

-- 1. Añadir columnas nuevas (nullable — Hibernate puede haberlas añadido ya si el app arrancó)
ALTER TABLE venta ADD COLUMN IF NOT EXISTS cliente_id VARCHAR(255);
ALTER TABLE venta ADD COLUMN IF NOT EXISTS cliente_tipo_documento VARCHAR(255);

-- 2. Poblar desde tabla clientes (toma el primer match por id)
--    Si la tabla venta está vacía este UPDATE no hace nada y los pasos 3-5 igual funcionan.
UPDATE venta v
SET (
    cliente_id,
    cliente_tipo_documento
) = (
    SELECT c.id, c.tipo_documento
    FROM clientes c
    LIMIT 1
)
WHERE v.cliente_id IS NULL;

-- 3. Forzar NOT NULL una vez pobladas (solo si NO hay filas sin asignar)
ALTER TABLE venta ALTER COLUMN cliente_id SET NOT NULL;
ALTER TABLE venta ALTER COLUMN cliente_tipo_documento SET NOT NULL;

-- 4. Eliminar FK antigua sobre cliente_id (si existe) antes de agregar la compuesta
DO $$
DECLARE v_constraint TEXT;
BEGIN
    SELECT tc.constraint_name INTO v_constraint
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY'
      AND tc.table_name = 'venta'
      AND kcu.column_name = 'cliente_id'
    LIMIT 1;
    IF v_constraint IS NOT NULL THEN
        EXECUTE 'ALTER TABLE venta DROP CONSTRAINT ' || quote_ident(v_constraint);
    END IF;
END $$;

-- 5. Agregar nueva FK compuesta
ALTER TABLE venta
    ADD CONSTRAINT fk_venta_cliente
    FOREIGN KEY (cliente_id, cliente_tipo_documento)
    REFERENCES clientes(id, tipo_documento);

-- 5.1 Asegurar FK nullable para orden_de_servicio_id -> orden_de_servicio(id)
--     Primero limpiar valores huérfanos para poder crear la constraint.
UPDATE venta v
SET orden_de_servicio_id = NULL
WHERE v.orden_de_servicio_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM orden_de_servicio o
      WHERE o.id = v.orden_de_servicio_id
  );

DO $$
DECLARE v_os_constraint TEXT;
BEGIN
    SELECT tc.constraint_name INTO v_os_constraint
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
    WHERE tc.constraint_type = 'FOREIGN KEY'
      AND tc.table_name = 'venta'
      AND kcu.column_name = 'orden_de_servicio_id'
    LIMIT 1;

    IF v_os_constraint IS NOT NULL THEN
        EXECUTE 'ALTER TABLE venta DROP CONSTRAINT ' || quote_ident(v_os_constraint);
    END IF;
END $$;

ALTER TABLE venta
    ADD CONSTRAINT fk_venta_orden_servicio
    FOREIGN KEY (orden_de_servicio_id)
    REFERENCES orden_de_servicio(id)
    ON DELETE SET NULL;

-- 6. Eliminar columnas de texto de comprador (datos movidos a FK)
ALTER TABLE venta DROP COLUMN IF EXISTS nombre_comprador;
ALTER TABLE venta DROP COLUMN IF EXISTS telefono_comprador;
ALTER TABLE venta DROP COLUMN IF EXISTS email_comprador;

-- 7. Eliminar columnas legacy de producto directo (si aún existen en DB)
ALTER TABLE venta DROP COLUMN IF EXISTS product_id;
ALTER TABLE venta DROP COLUMN IF EXISTS cantidad;
ALTER TABLE venta DROP COLUMN IF EXISTS precio_unitario;

-- 8. Eliminar total_venta (se calcula a partir de venta_detalle)
ALTER TABLE venta DROP COLUMN IF EXISTS total_venta;

-- Verificación final
SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_name = 'venta'
ORDER BY ordinal_position;
