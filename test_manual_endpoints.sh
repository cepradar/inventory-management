#!/bin/bash

# Script para validar la implementación de Auditoría y Ventas
# Ejecución: bash test_manual_endpoints.sh

echo "=========================================="
echo "VALIDACIÓN DE ENDPOINTS - AUDITORÍA"
echo "=========================================="
echo ""

# Obtener token de prueba
echo "1. Obteniendo token JWT..."
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}' | jq -r '.token')
echo "Token obtenido: ${TOKEN:0:20}..."
echo ""

# Endpoint: Obtener todos los movimientos
echo "2. GET /api/auditoria/movimientos"
curl -s -X GET http://localhost:8080/api/auditoria/movimientos \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.' | head -30
echo ""

# Endpoint: Obtener movimientos por producto
echo "3. GET /api/auditoria/producto/{productId}"
curl -s -X GET "http://localhost:8080/api/auditoria/producto/PROD001" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.' | head -20
echo ""

# Endpoint: Obtener movimientos por tipo
echo "4. GET /api/auditoria/tipo/INGRESO"
curl -s -X GET "http://localhost:8080/api/auditoria/tipo/INGRESO" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.' | head -20
echo ""

echo "=========================================="
echo "VALIDACIÓN DE ENDPOINTS - VENTAS"
echo "=========================================="
echo ""

# Endpoint: Listar todas las ventas
echo "5. GET /api/ventas/listar"
curl -s -X GET http://localhost:8080/api/ventas/listar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.' | head -30
echo ""

# Endpoint: Registrar nueva venta
echo "6. POST /api/ventas/registrar"
curl -s -X POST http://localhost:8080/api/ventas/registrar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD001",
    "cantidad": 5,
    "precioUnitario": 100.00,
    "nombreComprador": "Juan Pérez",
    "telefonoComprador": "555-1234",
    "emailComprador": "juan@example.com",
    "observaciones": "Venta de prueba"
  }' | jq '.'
echo ""

# Endpoint: Obtener ventas por producto
echo "7. GET /api/ventas/producto/{productId}"
curl -s -X GET "http://localhost:8080/api/ventas/producto/PROD001" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.' | head -20
echo ""

# Endpoint: Obtener total de ventas en rango
echo "8. GET /api/ventas/total/rango"
curl -s -X GET "http://localhost:8080/api/ventas/total/rango?fechaInicio=2026-01-01&fechaFin=2026-12-31" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq '.'
echo ""

echo "=========================================="
echo "VALIDACIÓN COMPLETADA"
echo "=========================================="
echo ""
echo "✓ Todos los endpoints fueron probados"
echo "✓ Verifica en la consola las respuestas JSON"
echo "✓ Asegúrate de que el token es válido"
echo "✓ Verifica que los datos se guardaron correctamente"
