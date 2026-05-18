-- =============================================================================
-- Migración v3: Expansión del sistema RBAC granular de permisos
-- Base de datos: SERVI (PostgreSQL)
-- Fecha: ver control de versiones
-- IMPORTANTE: Ejecutar en orden. Hacer backup antes de ejecutar.
-- =============================================================================

BEGIN;

-- -----------------------------------------------------------------------------
-- 1. Tabla roles: agregar campos active, permissions_version, timestamps
-- -----------------------------------------------------------------------------

ALTER TABLE roles
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS permissions_version BIGINT NOT NULL DEFAULT 1,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- -----------------------------------------------------------------------------
-- 2. Tabla permisos: agregar campos granulares; mantener columna name (alias)
-- -----------------------------------------------------------------------------

ALTER TABLE permisos
    ADD COLUMN IF NOT EXISTS code VARCHAR(120),
    ADD COLUMN IF NOT EXISTS label VARCHAR(180),
    ADD COLUMN IF NOT EXISTS module_key VARCHAR(80),
    ADD COLUMN IF NOT EXISTS category_key VARCHAR(80),
    ADD COLUMN IF NOT EXISTS action_key VARCHAR(80),
    ADD COLUMN IF NOT EXISTS ui_visible BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS critical BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT true,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- Inicializar code desde name para filas existentes
UPDATE permisos SET code = name WHERE code IS NULL;

-- Ahora que code está poblado, aplicar restricción
ALTER TABLE permisos
    ALTER COLUMN code SET NOT NULL;

DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uq_permisos_code'
    ) THEN
        ALTER TABLE permisos ADD CONSTRAINT uq_permisos_code UNIQUE (code);
    END IF;
END $$;

-- Inicializar label, module_key, action_key desde name para filas existentes
UPDATE permisos
SET
    label      = COALESCE(label, name),
    module_key = COALESCE(module_key, split_part(name, '.', 1)),
    action_key = COALESCE(action_key, split_part(name, '.', 2))
WHERE label IS NULL OR module_key IS NULL OR action_key IS NULL;

-- Ahora aplicar NOT NULL
ALTER TABLE permisos
    ALTER COLUMN label SET NOT NULL,
    ALTER COLUMN module_key SET NOT NULL,
    ALTER COLUMN action_key SET NOT NULL;

-- -----------------------------------------------------------------------------
-- 3. Renombrar tabla permisos_usuario → role_permissions
--    (si la tabla antigua existe y la nueva aún no)
-- -----------------------------------------------------------------------------

DO $$ BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'permisos_usuario' AND table_schema = 'public'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'role_permissions' AND table_schema = 'public'
    ) THEN
        ALTER TABLE permisos_usuario RENAME TO role_permissions;
    END IF;
END $$;

-- Agregar columnas nuevas a role_permissions
ALTER TABLE role_permissions
    ADD COLUMN IF NOT EXISTS granted_by VARCHAR(120),
    ADD COLUMN IF NOT EXISTS granted_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW();

-- -----------------------------------------------------------------------------
-- 4. Crear tabla permission_audit_log (nueva)
-- -----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS permission_audit_log (
    id              BIGSERIAL PRIMARY KEY,
    role_name       VARCHAR(120) NOT NULL,
    permission_code VARCHAR(120) NOT NULL,
    changed_by      VARCHAR(120) NOT NULL,
    changed_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    old_value       BOOLEAN,
    new_value       BOOLEAN NOT NULL,
    reason          VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_pal_role_name  ON permission_audit_log (role_name);
CREATE INDEX IF NOT EXISTS idx_pal_changed_at ON permission_audit_log (changed_at);

-- -----------------------------------------------------------------------------
-- 5. Datos semilla: permisos del catálogo
--    Inserta solo si no existen (idempotente por ON CONFLICT DO NOTHING).
-- -----------------------------------------------------------------------------

INSERT INTO permisos (code, label, module_key, category_key, action_key, ui_visible, critical, active)
VALUES
  -- Módulo: users
  ('users.read',           'Ver usuarios',               'users',     'management', 'read',   true, false, true),
  ('users.create',         'Crear usuarios',             'users',     'management', 'create', true, false, true),
  ('users.update',         'Editar usuarios',            'users',     'management', 'update', true, false, true),
  ('users.delete',         'Eliminar usuarios',          'users',     'management', 'delete', true, true,  true),
  ('users.reset_password', 'Resetear contraseña',        'users',     'management', 'reset_password', true, true, true),
  ('users.assign_role',    'Asignar roles',              'users',     'management', 'assign_role', true, true, true),

  -- Módulo: inventory
  ('inventory.read',       'Ver inventario',             'inventory', null,         'read',   true, false, true),
  ('inventory.create',     'Agregar productos',          'inventory', null,         'create', true, false, true),
  ('inventory.update',     'Editar productos',           'inventory', null,         'update', true, false, true),
  ('inventory.delete',     'Eliminar productos',         'inventory', null,         'delete', true, true,  true),
  ('inventory.export',     'Exportar inventario',        'inventory', null,         'export', true, false, true),

  -- Módulo: clients
  ('clients.read',         'Ver clientes',               'clients',   null,         'read',   true, false, true),
  ('clients.create',       'Crear clientes',             'clients',   null,         'create', true, false, true),
  ('clients.update',       'Editar clientes',            'clients',   null,         'update', true, false, true),
  ('clients.delete',       'Eliminar clientes',          'clients',   null,         'delete', true, true,  true),

  -- Módulo: sales
  ('sales.read',           'Ver ventas',                 'sales',     null,         'read',   true, false, true),
  ('sales.create',         'Registrar ventas',           'sales',     null,         'create', true, false, true),
  ('sales.invoice.pdf',    'Generar factura PDF',        'sales',     'documents',  'invoice.pdf', true, false, true),
  ('sales.delete',         'Anular ventas',              'sales',     null,         'delete', true, true,  true),

  -- Módulo: orders (Órdenes de Servicio)
  ('orders.read',          'Ver órdenes de servicio',    'orders',    null,         'read',   true, false, true),
  ('orders.create',        'Crear órdenes de servicio',  'orders',    null,         'create', true, false, true),
  ('orders.update',        'Editar órdenes de servicio', 'orders',    null,         'update', true, false, true),
  ('orders.delete',        'Eliminar órdenes',           'orders',    null,         'delete', true, true,  true),
  ('orders.assign_tech',   'Asignar técnico',            'orders',    null,         'assign_tech', true, false, true),
  ('orders.pdf',           'Generar PDF de orden',       'orders',    'documents',  'pdf',    true, false, true),

  -- Módulo: audit
  ('audit.read',           'Ver auditoría',              'audit',     null,         'read',   true, false, true),
  ('audit.export',         'Exportar auditoría',         'audit',     null,         'export', true, false, true),

  -- Módulo: config
  ('config.roles.read',    'Ver roles y permisos',       'config',    'roles',      'read',   true, false, true),
  ('config.roles.write',   'Gestionar roles y permisos', 'config',    'roles',      'write',  true, true,  true),
  ('config.company.read',  'Ver configuración empresa',  'config',    'company',    'read',   true, false, true),
  ('config.company.write', 'Editar configuración empresa','config',   'company',    'write',  true, true,  true),

  -- Módulo: reports
  ('reports.read',         'Ver reportes',               'reports',   null,         'read',   true, false, true),
  ('reports.export',       'Exportar reportes',          'reports',   null,         'export', true, false, true)

ON CONFLICT (code) DO NOTHING;

-- Actualizar name = code para mantener retrocompatibilidad
UPDATE permisos SET name = code WHERE name IS NULL OR name <> code;

COMMIT;
