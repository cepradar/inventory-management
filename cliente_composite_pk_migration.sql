-- Composite PK migration for clientes (documento + tipo_documento)
-- Review and run in a transaction when possible.

-- 1) Add cliente_tipo_documento columns (if missing)
ALTER TABLE cliente_electrodomesticos
  ADD COLUMN IF NOT EXISTS cliente_tipo_documento VARCHAR(20);

ALTER TABLE orden_de_servicio
  ADD COLUMN IF NOT EXISTS cliente_tipo_documento VARCHAR(20);

ALTER TABLE eventos_producto
  ADD COLUMN IF NOT EXISTS cliente_tipo_documento VARCHAR(20);

-- 2) Backfill cliente_tipo_documento
UPDATE cliente_electrodomesticos ce
SET cliente_tipo_documento = c.tipo_documento
FROM clientes c
WHERE ce.cliente_id = c.id
  AND (ce.cliente_tipo_documento IS NULL OR ce.cliente_tipo_documento = '');

UPDATE orden_de_servicio os
SET cliente_tipo_documento = c.tipo_documento
FROM clientes c
WHERE os.cliente_id = c.id
  AND (os.cliente_tipo_documento IS NULL OR os.cliente_tipo_documento = '');

UPDATE eventos_producto ep
SET cliente_tipo_documento = c.tipo_documento
FROM clientes c
WHERE ep.cliente_id = c.id
  AND (ep.cliente_tipo_documento IS NULL OR ep.cliente_tipo_documento = '');

-- 3) Enforce NOT NULL where required
ALTER TABLE cliente_electrodomesticos
  ALTER COLUMN cliente_tipo_documento SET NOT NULL;

ALTER TABLE orden_de_servicio
  ALTER COLUMN cliente_tipo_documento SET NOT NULL;

-- 4) Drop existing FKs that reference clientes (by single-column key)
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT conname, conrelid::regclass AS table_name
    FROM pg_constraint
    WHERE contype = 'f'
      AND confrelid = 'clientes'::regclass
      AND conrelid IN (
        'cliente_electrodomesticos'::regclass,
        'orden_de_servicio'::regclass,
        'eventos_producto'::regclass
      )
  LOOP
    EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', r.table_name, r.conname);
  END LOOP;
END $$;

-- 5) Drop existing PK on clientes and create composite PK
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT conname
    FROM pg_constraint
    WHERE contype = 'p'
      AND conrelid = 'clientes'::regclass
  LOOP
    EXECUTE format('ALTER TABLE clientes DROP CONSTRAINT %I', r.conname);
  END LOOP;
END $$;

ALTER TABLE clientes
  ADD CONSTRAINT clientes_pkey PRIMARY KEY (id, tipo_documento);

-- 6) Add composite FKs to clientes
ALTER TABLE cliente_electrodomesticos
  ADD CONSTRAINT cliente_electrodomesticos_cliente_fkey
  FOREIGN KEY (cliente_id, cliente_tipo_documento)
  REFERENCES clientes(id, tipo_documento);

ALTER TABLE orden_de_servicio
  ADD CONSTRAINT orden_de_servicio_cliente_fkey
  FOREIGN KEY (cliente_id, cliente_tipo_documento)
  REFERENCES clientes(id, tipo_documento);

ALTER TABLE eventos_producto
  ADD CONSTRAINT eventos_producto_cliente_fkey
  FOREIGN KEY (cliente_id, cliente_tipo_documento)
  REFERENCES clientes(id, tipo_documento);
