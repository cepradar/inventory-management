package com.inventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class OrdenServicioEstadoConstraintInitializer {

    private static final Logger logger = LoggerFactory.getLogger(OrdenServicioEstadoConstraintInitializer.class);

    @Bean
    CommandLineRunner initOrdenServicioEstadoConstraint(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                // Garantiza los codigos de tipo_evento para ORDEN (categoria_id = 2).
                jdbcTemplate.execute("""
                    INSERT INTO tipo_evento (id, nombre, categoria_id) VALUES
                    ('SOC', 'ORDEN_SERVICIO_CREADA', 2),
                    ('SOA', 'ORDEN_SERVICIO_ASIGNADA', 2),
                    ('SOE', 'ORDEN_SERVICIO_EN_PROCESO', 2),
                    ('SOP', 'ORDEN_SERVICIO_PAUSADA', 2),
                    ('SOD', 'ORDEN_SERVICIO_DIAGNOSTICADA', 2),
                    ('SOR', 'ORDEN_SERVICIO_REPARADA', 2),
                    ('SOT', 'ORDEN_SERVICIO_PRUEBA', 2),
                    ('SOL', 'ORDEN_SERVICIO_LISTA', 2),
                    ('SOENT', 'ORDEN_SERVICIO_ENTREGADA', 2),
                    ('SOCAN', 'ORDEN_SERVICIO_CANCELADA', 2),
                    ('SOREC', 'ORDEN_SERVICIO_RECHAZADA', 2)
                    ON CONFLICT (id)
                    DO UPDATE SET
                        nombre = EXCLUDED.nombre,
                        categoria_id = EXCLUDED.categoria_id
                    """);

                // Normaliza datos legacy para que estado guarde el codigo de tipo_evento (categoria 2).
                jdbcTemplate.execute("""
                    UPDATE orden_de_servicio os
                    SET estado = CASE UPPER(TRIM(os.estado))
                        WHEN 'RECIBIDO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_CREADA' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        WHEN 'EN_PROCESO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_EN_PROCESO' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        WHEN 'EN_DIAGNOSTICO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_DIAGNOSTICADA' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        WHEN 'REPARADO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_REPARADA' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        WHEN 'LISTO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_LISTA' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        WHEN 'ENTREGADO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_ENTREGADA' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        WHEN 'CANCELADO' THEN COALESCE((SELECT te.id FROM tipo_evento te WHERE te.nombre = 'ORDEN_SERVICIO_CANCELADA' AND te.categoria_id = 2 LIMIT 1), os.estado)
                        ELSE os.estado
                    END
                    """);

                // Completa estados nulos o no validos con el estado inicial de orden (SOC).
                jdbcTemplate.execute("""
                    UPDATE orden_de_servicio os
                    SET estado = 'SOC'
                    WHERE os.estado IS NULL
                       OR NOT EXISTS (
                            SELECT 1
                            FROM tipo_evento te
                            WHERE te.id = os.estado
                              AND te.categoria_id = 2
                       )
                    """);

                // FK de orden_de_servicio.estado -> tipo_evento.id
                jdbcTemplate.execute("""
                    DO $$
                    BEGIN
                        IF NOT EXISTS (
                            SELECT 1
                            FROM pg_constraint
                            WHERE conname = 'fk_orden_servicio_estado_tipo_evento'
                        ) THEN
                            ALTER TABLE orden_de_servicio
                            ADD CONSTRAINT fk_orden_servicio_estado_tipo_evento
                            FOREIGN KEY (estado)
                            REFERENCES tipo_evento(id);
                        END IF;
                    END $$;
                    """);

                // Trigger para asegurar que estado pertenezca a categoria_id = 2 (ORDEN).
                jdbcTemplate.execute("""
                    CREATE OR REPLACE FUNCTION validar_estado_orden_categoria()
                    RETURNS trigger AS $$
                    BEGIN
                        IF NOT EXISTS (
                            SELECT 1
                            FROM tipo_evento te
                            WHERE te.id = NEW.estado
                              AND te.categoria_id = 2
                        ) THEN
                            RAISE EXCEPTION 'Estado % no pertenece a tipo_evento categoria_id=2', NEW.estado;
                        END IF;
                        RETURN NEW;
                    END;
                    $$ LANGUAGE plpgsql;
                    """);

                jdbcTemplate.execute("""
                    DO $$
                    BEGIN
                        IF EXISTS (
                            SELECT 1 FROM pg_trigger
                            WHERE tgname = 'trg_validar_estado_orden_categoria'
                        ) THEN
                            DROP TRIGGER trg_validar_estado_orden_categoria ON orden_de_servicio;
                        END IF;

                        CREATE TRIGGER trg_validar_estado_orden_categoria
                        BEFORE INSERT OR UPDATE OF estado
                        ON orden_de_servicio
                        FOR EACH ROW
                        EXECUTE FUNCTION validar_estado_orden_categoria();
                    END $$;
                    """);

                logger.info("✅ Constraint y validación de estado de orden inicializados correctamente");
            } catch (Exception ex) {
                logger.error("❌ Error inicializando constraint de estado de orden: {}", ex.getMessage(), ex);
            }
        };
    }
}
