# Configuración del Proyecto

Esta carpeta contiene archivos de configuración sensibles que **NO deben versionarse en Git**.

## Archivos de Configuración

### `application.properties` ⚠️ NO VERSIONAR
Archivo principal de configuración de Spring Boot. Contiene:
- Credenciales de PostgreSQL
- Secret JWT
- Patrones CORS permitidos
- Configuración JPA/Hibernate
- Límites de archivos

**Ubicación en producción:** `C:\SERVI-APP\inventory-management\config\application.properties`

### `application.example.properties` ✅ SÍ VERSIONAR
Plantilla con valores de ejemplo. Copiar a `application.properties` y completar con valores reales.

### `deploy.env.properties` ⚠️ NO VERSIONAR
Configuración para el script de deploy (`scripts/deploy.ps1`). Contiene:
- `REPO_PATH`: Ruta del repositorio en el servidor
- `BACKEND_SERVICE_NAME`: Nombre del servicio Windows
- `BACKEND_JAR_PATTERN`: Patrón del JAR compilado
- `IIS_SITE_PATH`: Ruta del sitio IIS para el frontend

### `app.env.properties` 🗑️ DEPRECADO
**⚠️ Este archivo está deprecado.** Usar `application.properties` en su lugar.
Solo se mantiene por compatibilidad temporal.

## Cómo Configurar en Servidor

### Configuración Inicial (Primera Vez)

1. **Clonar repositorio:**
   ```powershell
   git clone https://github.com/cepradar/inventory-management.git C:\SERVI-APP\inventory-management
   cd C:\SERVI-APP\inventory-management
   ```

2. **Ejecutar script de configuración inicial:**
   ```powershell
   .\scripts\setup-server.ps1
   ```
   
   El script te pedirá:
   - Contraseña de PostgreSQL
   - JWT secret (o generará uno automático)
   
   Y automáticamente:
   - Crea `config\application.properties`
   - Valida conexión a PostgreSQL
   - Te indica los próximos pasos

3. **Configurar servicio NSSM:**
   ```powershell
   nssm set servi-backend AppDirectory "C:\SERVI-APP\inventory-management"
   ```

4. **Desplegar aplicación:**
   ```powershell
   .\scripts\deploy.ps1
   ```

### Configuración Manual (Alternativa)

Si prefieres configurar manualmente:

1. **Copiar plantilla:**
   ```powershell
   Copy-Item config\application.example.properties config\application.properties
   ```

2. **Editar credenciales:**
   Abrir `config\application.properties` y completar:
   - `spring.datasource.password=<tu_password_postgresql>`
   - `jwt.secret=<secreto_largo_y_seguro>`

3. **Continuar con paso 3 de Configuración Inicial**

## Verificación

Para verificar que el backend está leyendo correctamente la configuración:

```powershell
# Ver logs del servicio
Get-Content C:\SERVI-APP\inventory-management\logs\application.log -Tail 50

# Verificar conexión DB
psql -h localhost -U postgres -d SERVI -c "SELECT version();"
```

## .gitignore

Los siguientes archivos están ignorados en Git:
- `config/*.properties` (excepto `*.example.properties`)
- `config/*.env`
- `config/*.secrets`
- `config/*.local`
