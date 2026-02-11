# =============================================
# Script de Configuración Inicial
# Ejecutar UNA VEZ después de clonar el repositorio
# =============================================

$ErrorActionPreference = 'Stop'

Write-Host "=== CONFIGURACION INICIAL DEL SERVIDOR ===" -ForegroundColor Cyan
Write-Host ""

$configPath = "config\application.properties"
$examplePath = "config\application.example.properties"

# 1. Verificar que existe el ejemplo
if (-not (Test-Path $examplePath)) {
    Write-Host "[ERROR] No se encontro $examplePath" -ForegroundColor Red
    exit 1
}

# 2. Crear config/application.properties desde ejemplo
if (Test-Path $configPath) {
    Write-Host "[INFO] $configPath ya existe" -ForegroundColor Yellow
    $overwrite = Read-Host "Deseas sobrescribirlo? (s/N)"
    if ($overwrite -ne 's' -and $overwrite -ne 'S') {
        Write-Host "Operacion cancelada." -ForegroundColor Yellow
        exit 0
    }
}

Write-Host "Copiando plantilla de configuracion..." -ForegroundColor Cyan
Copy-Item $examplePath $configPath -Force
Write-Host "[OK] Archivo creado: $configPath" -ForegroundColor Green
Write-Host ""

# 3. Solicitar credenciales
Write-Host "=== CONFIGURAR CREDENCIALES ===" -ForegroundColor Cyan
Write-Host ""

$dbPassword = Read-Host "Ingresa la contrasena de PostgreSQL"
if (-not $dbPassword) {
    Write-Host "[ERROR] La contrasena no puede estar vacia" -ForegroundColor Red
    exit 1
}

$jwtSecret = Read-Host "Ingresa el JWT secret (minimo 32 caracteres)"
if ($jwtSecret.Length -lt 32) {
    Write-Host "[WARN] JWT secret muy corto. Generando uno aleatorio..." -ForegroundColor Yellow
    $jwtSecret = [System.Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(48))
    Write-Host "JWT secret generado: $jwtSecret" -ForegroundColor Green
}

# 4. Actualizar archivo
Write-Host ""
Write-Host "Actualizando credenciales..." -ForegroundColor Cyan

$content = Get-Content $configPath
$content = $content -replace '(spring\.datasource\.password=).*', "`$1$dbPassword"
$content = $content -replace '(jwt\.secret=).*', "`$1$jwtSecret"
$content | Set-Content $configPath

Write-Host "[OK] Credenciales configuradas" -ForegroundColor Green
Write-Host ""

# 5. Verificar conexion a DB
Write-Host "Verificando conexion a PostgreSQL..." -ForegroundColor Cyan
$env:PGPASSWORD = $dbPassword
try {
    $result = & psql -h localhost -U postgres -d SERVI -c "SELECT 1;" 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "[OK] Conexion exitosa" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] No se pudo conectar a PostgreSQL" -ForegroundColor Red
        Write-Host $result -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Verifica que PostgreSQL esta corriendo y que la contrasena es correcta." -ForegroundColor Yellow
    }
} finally {
    Remove-Item Env:\PGPASSWORD -ErrorAction SilentlyContinue
}

Write-Host ""
Write-Host "=== CONFIGURACION COMPLETADA ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "Siguiente paso:" -ForegroundColor White
Write-Host "  .\scripts\deploy.ps1" -ForegroundColor Gray
