# =============================================
# Script de Verificación Pre-Deploy
# Ejecutar ANTES de iniciar el servicio backend
# =============================================

$ErrorActionPreference = 'Stop'

Write-Host "=== VERIFICACION DE CONFIGURACION ===" -ForegroundColor Cyan
Write-Host ""

# 1. Verificar que existe config/application.properties
$configPath = "config\application.properties"
if (-not (Test-Path $configPath)) {
    Write-Host "[ERROR] No existe $configPath" -ForegroundColor Red
    Write-Host "Copia config\application.example.properties a config\application.properties" -ForegroundColor Yellow
    exit 1
}
Write-Host "[OK] Archivo de configuracion encontrado: $configPath" -ForegroundColor Green

# 2. Verificar que tiene password de DB
$dbPassword = Get-Content $configPath | Where-Object { $_ -match 'spring\.datasource\.password=' }
if (-not $dbPassword -or $dbPassword -match 'spring\.datasource\.password=\s*$') {
    Write-Host "[ERROR] No hay password de PostgreSQL configurada" -ForegroundColor Red
    Write-Host "Edita $configPath y agrega: spring.datasource.password=TU_PASSWORD" -ForegroundColor Yellow
    exit 1
}
Write-Host "[OK] Password de DB configurada" -ForegroundColor Green

# 3. Verificar que tiene JWT secret
$jwtSecret = Get-Content $configPath | Where-Object { $_ -match 'jwt\.secret=' }
if (-not $jwtSecret -or $jwtSecret -match 'jwt\.secret=\s*$') {
    Write-Host "[ERROR] No hay JWT secret configurado" -ForegroundColor Red
    Write-Host "Edita $configPath y agrega: jwt.secret=TU_SECRETO_LARGO" -ForegroundColor Yellow
    exit 1
}
Write-Host "[OK] JWT secret configurado" -ForegroundColor Green

# 4. Verificar conexion a PostgreSQL
Write-Host ""
Write-Host "Probando conexion a PostgreSQL..." -ForegroundColor Cyan

$dbUrl = (Get-Content $configPath | Where-Object { $_ -match 'spring\.datasource\.url=' }) -replace '.*spring\.datasource\.url=', ''
$dbUser = (Get-Content $configPath | Where-Object { $_ -match 'spring\.datasource\.username=' }) -replace '.*spring\.datasource\.username=', ''
$dbName = if ($dbUrl -match 'jdbc:postgresql://[^/]+/(\w+)') { $matches[1] } else { 'SERVI' }

try {
    $env:PGPASSWORD = ($dbPassword -replace '.*spring\.datasource\.password=', '').Trim()
    $result = & psql -h localhost -U $dbUser -d $dbName -c "SELECT version();" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Conexion exitosa a PostgreSQL" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] No se pudo conectar a PostgreSQL" -ForegroundColor Red
        Write-Host $result -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "[ERROR] Error al probar conexion: $_" -ForegroundColor Red
    exit 1
} finally {
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

# 5. Verificar que existe el JAR compilado
Write-Host ""
$jarPattern = "target\inventory-management-*.jar"
$jar = Get-ChildItem -Path $jarPattern -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $jar) {
    Write-Host "[WARN] No se encontro JAR compilado en target/" -ForegroundColor Yellow
    Write-Host "Ejecuta: .\mvnw.cmd package" -ForegroundColor Yellow
} else {
    Write-Host "[OK] JAR encontrado: $($jar.Name)" -ForegroundColor Green
}

# 6. Verificar configuracion NSSM (si existe el servicio)
Write-Host ""
$serviceName = "servi-backend"
try {
    $service = Get-Service -Name $serviceName -ErrorAction Stop
    Write-Host "[OK] Servicio '$serviceName' existe" -ForegroundColor Green
    
    $appDir = & nssm get $serviceName AppDirectory 2>&1
    $currentDir = (Get-Location).Path
    
    if ($appDir -ne $currentDir) {
        Write-Host "[WARN] AppDirectory del servicio: $appDir" -ForegroundColor Yellow
        Write-Host "[WARN] Directorio actual: $currentDir" -ForegroundColor Yellow
        Write-Host "Ejecuta: nssm set $serviceName AppDirectory `"$currentDir`"" -ForegroundColor Yellow
    } else {
        Write-Host "[OK] AppDirectory configurado correctamente" -ForegroundColor Green
    }
} catch {
    Write-Host "[INFO] Servicio '$serviceName' no encontrado (OK si es primera instalacion)" -ForegroundColor Gray
}

Write-Host ""
Write-Host "=== VERIFICACION COMPLETADA ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Para iniciar el backend:" -ForegroundColor White
Write-Host "  1. Compilar: .\mvnw.cmd package" -ForegroundColor Gray
Write-Host "  2. Servicio: sc start $serviceName" -ForegroundColor Gray
Write-Host "  O manual: .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
