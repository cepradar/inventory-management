$ErrorActionPreference = 'Stop'

Write-Host "=== VERIFICACION PRE-DEPLOY ===" -ForegroundColor Cyan
& "$PSScriptRoot\verify-config.ps1"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Verificacion fallo. Corrige los errores antes de continuar." -ForegroundColor Red
    exit 1
}
Write-Host ""

function Get-ConfigValue {
    param(
        [string]$ConfigPath,
        [string]$Key,
        [string]$DefaultValue = ''
    )

    if (-not (Test-Path $ConfigPath)) {
        return $DefaultValue
    }

    $line = Get-Content $ConfigPath | ForEach-Object {
        $current = $_.Trim()
        $current = $current.Trim([char]0xFEFF)
        if (-not $current) { return $null }
        if ($current.StartsWith('#') -or $current.StartsWith(';')) { return $null }
        return $current
    } | Where-Object { $_ -match "^${Key}\s*=" } | Select-Object -First 1
    if (-not $line) {
        return $DefaultValue
    }

    return ($line -replace "^${Key}\s*=", '').Trim()
}

$configPath = Join-Path $PSScriptRoot '..\config\deploy.env.properties'

$repoPath = Get-ConfigValue -ConfigPath $configPath -Key 'REPO_PATH' -DefaultValue $env:REPO_PATH
$backendService = Get-ConfigValue -ConfigPath $configPath -Key 'BACKEND_SERVICE_NAME' -DefaultValue 'inventory-backend'
$backendJarPattern = Get-ConfigValue -ConfigPath $configPath -Key 'BACKEND_JAR_PATTERN' -DefaultValue 'target\inventory-management-*.jar'
$frontendDistPath = Get-ConfigValue -ConfigPath $configPath -Key 'FRONTEND_DIST_PATH' -DefaultValue 'inventory-frontend\dist'
$iisPath = Get-ConfigValue -ConfigPath $configPath -Key 'IIS_SITE_PATH' -DefaultValue 'C:\inetpub\wwwroot\inventory-frontend'

if (-not $repoPath) {
    if (-not (Test-Path $configPath)) {
        throw "No se encontro el archivo de configuracion: $configPath"
    }
    throw 'REPO_PATH no esta configurado en config\deploy.env.properties o en la variable de entorno REPO_PATH.'
}

Set-Location $repoPath

git pull origin main

Write-Host 'Construyendo backend...'
.\mvnw.cmd -DskipTests package

Write-Host 'Construyendo frontend...'
Set-Location (Join-Path $repoPath 'inventory-frontend')
if (-not (Test-Path 'node_modules')) {
    npm install
} else {
    npm install
}

npm run build

Write-Host 'Publicando frontend en IIS...'
$distFullPath = Join-Path $repoPath $frontendDistPath
if (-not (Test-Path $distFullPath)) {
    throw "No se encontro el build en $distFullPath"
}

if (-not (Test-Path $iisPath)) {
    New-Item -ItemType Directory -Force -Path $iisPath | Out-Null
}

robocopy $distFullPath $iisPath /E | Out-Null

Write-Host 'Reiniciando servicio backend...'
try {
    $service = Get-Service -Name $backendService -ErrorAction Stop
    if ($service.Status -eq 'Running') {
        nssm restart $backendService
    } else {
        nssm start $backendService
    }
} catch {
    Write-Host "Servicio $backendService no encontrado. Configuralo con NSSM." -ForegroundColor Yellow
}

Write-Host 'Deploy finalizado.'
