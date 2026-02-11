# =============================================
# Script para Reiniciar AnyDesk
# =============================================

$ErrorActionPreference = 'Stop'

Write-Host "=== REINICIANDO ANYDESK ===" -ForegroundColor Cyan
Write-Host ""

$serviceName = "AnyDesk"

try {
    # 1. Verificar si el servicio existe
    $service = Get-Service -Name $serviceName -ErrorAction Stop
    Write-Host "[INFO] Servicio encontrado: $serviceName (Estado: $($service.Status))" -ForegroundColor Gray
    Write-Host ""
    
    # 2. Detener el servicio si está corriendo
    if ($service.Status -eq 'Running') {
        Write-Host "Deteniendo servicio AnyDesk..." -ForegroundColor Yellow
        Stop-Service -Name $serviceName -Force
        Start-Sleep -Seconds 2
        Write-Host "[OK] Servicio detenido" -ForegroundColor Green
    } else {
        Write-Host "[INFO] Servicio ya estaba detenido" -ForegroundColor Gray
    }
    
    # 3. Cerrar procesos de AnyDesk que puedan quedar
    Write-Host ""
    Write-Host "Cerrando procesos de AnyDesk..." -ForegroundColor Yellow
    $processes = Get-Process -Name "AnyDesk" -ErrorAction SilentlyContinue
    if ($processes) {
        $processes | ForEach-Object {
            Write-Host "  Cerrando proceso AnyDesk (PID: $($_.Id))" -ForegroundColor Gray
            Stop-Process -Id $_.Id -Force -ErrorAction SilentlyContinue
        }
        Start-Sleep -Seconds 1
        Write-Host "[OK] Procesos cerrados" -ForegroundColor Green
    } else {
        Write-Host "[INFO] No hay procesos de AnyDesk corriendo" -ForegroundColor Gray
    }
    
    # 4. Iniciar el servicio
    Write-Host ""
    Write-Host "Iniciando servicio AnyDesk..." -ForegroundColor Cyan
    Start-Service -Name $serviceName
    Start-Sleep -Seconds 3
    
    # 5. Verificar estado final
    $service.Refresh()
    if ($service.Status -eq 'Running') {
        Write-Host "[OK] AnyDesk reiniciado correctamente" -ForegroundColor Green
    } else {
        Write-Host "[WARN] Servicio en estado: $($service.Status)" -ForegroundColor Yellow
    }
    
} catch [Microsoft.PowerShell.Commands.ServiceCommandException] {
    Write-Host "[ERROR] Servicio '$serviceName' no encontrado" -ForegroundColor Red
    Write-Host ""
    Write-Host "Intentando reiniciar proceso de AnyDesk directamente..." -ForegroundColor Yellow
    
    # Cerrar procesos
    $processes = Get-Process -Name "AnyDesk" -ErrorAction SilentlyContinue
    if ($processes) {
        $processes | Stop-Process -Force
        Write-Host "[OK] Procesos cerrados" -ForegroundColor Green
    }
    
    # Buscar ejecutable de AnyDesk
    $anydeskPaths = @(
        "C:\Program Files (x86)\AnyDesk\AnyDesk.exe",
        "C:\Program Files\AnyDesk\AnyDesk.exe",
        "$env:ProgramData\AnyDesk\AnyDesk.exe",
        "$env:APPDATA\AnyDesk\AnyDesk.exe"
    )
    
    $anydeskExe = $anydeskPaths | Where-Object { Test-Path $_ } | Select-Object -First 1
    
    if ($anydeskExe) {
        Write-Host "Iniciando AnyDesk desde: $anydeskExe" -ForegroundColor Cyan
        Start-Process -FilePath $anydeskExe
        Write-Host "[OK] AnyDesk iniciado" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] No se encontro el ejecutable de AnyDesk" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "[ERROR] Error inesperado: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "=== OPERACION COMPLETADA ===" -ForegroundColor Cyan
