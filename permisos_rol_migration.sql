-- Add description to roles
ALTER TABLE roles
  ADD COLUMN IF NOT EXISTS description VARCHAR(255);

-- Ensure permisos table exists
CREATE TABLE IF NOT EXISTS permisos (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL UNIQUE
);

-- Ensure permisos_usuario maps role-permission
CREATE TABLE IF NOT EXISTS permisos_usuario (
  id BIGSERIAL PRIMARY KEY,
  role_name VARCHAR(255) NOT NULL,
  permission_id BIGINT NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT permisos_usuario_role_fk FOREIGN KEY (role_name) REFERENCES roles(name),
  CONSTRAINT permisos_usuario_perm_fk FOREIGN KEY (permission_id) REFERENCES permisos(id)
);

-- If legacy permisos_usuario has user_id, migrate structure
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'permisos_usuario'
      AND column_name = 'user_id'
  ) THEN
    ALTER TABLE permisos_usuario DROP CONSTRAINT IF EXISTS permisos_usuario_user_fk;
    ALTER TABLE permisos_usuario DROP COLUMN IF EXISTS user_id;
  END IF;
END $$;

-- Ensure role_name, permission_id, is_active exist
ALTER TABLE permisos_usuario
  ADD COLUMN IF NOT EXISTS role_name VARCHAR(255);

ALTER TABLE permisos_usuario
  ADD COLUMN IF NOT EXISTS permission_id BIGINT;

ALTER TABLE permisos_usuario
  ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT FALSE;

-- Drop legacy id PK and use composite key
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'permisos_usuario'
      AND column_name = 'id'
  ) THEN
    ALTER TABLE permisos_usuario DROP CONSTRAINT IF EXISTS permisos_usuario_pkey;
    ALTER TABLE permisos_usuario DROP COLUMN IF EXISTS id;
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'permisos_usuario_pkey'
      AND conrelid = 'permisos_usuario'::regclass
  ) THEN
    ALTER TABLE permisos_usuario
      ADD CONSTRAINT permisos_usuario_pkey PRIMARY KEY (role_name, permission_id);
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'permisos_usuario_role_fk'
      AND conrelid = 'permisos_usuario'::regclass
  ) THEN
    ALTER TABLE permisos_usuario
      ADD CONSTRAINT permisos_usuario_role_fk FOREIGN KEY (role_name) REFERENCES roles(name);
  END IF;
END $$;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1
    FROM pg_constraint
    WHERE conname = 'permisos_usuario_perm_fk'
      AND conrelid = 'permisos_usuario'::regclass
  ) THEN
    ALTER TABLE permisos_usuario
      ADD CONSTRAINT permisos_usuario_perm_fk FOREIGN KEY (permission_id) REFERENCES permisos(id);
  END IF;
END $$;
