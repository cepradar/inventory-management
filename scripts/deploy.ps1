# =============================================
# DEPLOY COMPLETO BACKEND + FRONTEND
# Ejecutar SOLO en el servidor
# =============================================

$ErrorActionPreference = 'Stop'

Write-Host "=== INICIANDO DEPLOY ===" -ForegroundColor Cyan
Write-Host ""

# -------------------------------------------------
# CONFIG
# -------------------------------------------------

$repoPath = "C:\SERVI-APP\inventory-management"
$backendService = "servi-backend"
$frontendPath = Join-Path $repoPath "inventory-frontend"
$frontendDistPath = Join-Path $frontendPath "dist"
$iisPath = "C:\inetpub\wwwroot\SERVI-FRONT"

Set-Location $repoPath

# -------------------------------------------------
# VERIFICAR CONFIG
# -------------------------------------------------

$configPath = Join-Path $repoPath "config\application.properties"

if (-not (Test-Path $configPath)) {
    throw "No existe config\application.properties"
}

$dbPassword = Get-Content $configPath | Where-Object { $_ -match 'spring\.datasource\.password=' }
if (-not $dbPassword -or $dbPassword -match 'spring\.datasource\.password=\s*$') {
    throw "No hay password de PostgreSQL configurada"
}

$jwtSecret = Get-Content $configPath | Where-Object { $_ -match 'jwt\.secret=' }
if (-not $jwtSecret -or $jwtSecret -match 'jwt\.secret=\s*$') {
    throw "No hay JWT secret configurado"
}

Write-Host "[OK] Configuracion validada" -ForegroundColor Green

# -------------------------------------------------
# ACTUALIZAR CODIGO
# -------------------------------------------------

Write-Host "Actualizando repositorio..."
git pull origin main

# -------------------------------------------------
# BACKEND
# -------------------------------------------------

Write-Host "Construyendo backend..."
.\mvnw.cmd -DskipTests package

if ($LASTEXITCODE -ne 0) {
    throw "Error construyendo backend"
}

Write-Host "[OK] Backend compilado" -ForegroundColor Green

# -------------------------------------------------
# FRONTEND
# -------------------------------------------------

Write-Host "Construyendo frontend..."
Set-Location $frontendPath

npm install
npm run build

if (-not (Test-Path $frontendDistPath)) {
    throw "No se genero el dist del frontend"
}

Write-Host "[OK] Frontend compilado" -ForegroundColor Green

# -------------------------------------------------
# PUBLICAR EN IIS
# -------------------------------------------------

Write-Host "Publicando frontend en IIS..."

if (-not (Test-Path $iisPath)) {
    New-Item -ItemType Directory -Force -Path $iisPath | Out-Null
}

robocopy $frontendDistPath $iisPath /E /NFL /NDL /NJH /NJS /NC /NS | Out-Null

Write-Host "[OK] Frontend publicado en IIS" -ForegroundColor Green

# -------------------------------------------------
# REINICIAR SERVICIO
# -------------------------------------------------

Write-Host "Reiniciando servicio backend..."

try {
    $service = Get-Service -Name $backendService -ErrorAction Stop

    if ($service.Status -eq "Running") {
        nssm restart $backendService
    } else {
        nssm start $backendService
    }

    Write-Host "[OK] Servicio reiniciado" -ForegroundColor Green
}
catch {
    Write-Host "[ERROR] Servicio no encontrado. Configuralo con NSSM." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== DEPLOY FINALIZADO CORRECTAMENTE ===" -ForegroundColor Cyan
