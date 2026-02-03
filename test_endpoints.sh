#!/bin/bash

# Script de prueba de endpoints - Auditoría y Ventas
# Asegúrate de tener un token válido en la variable TOKEN

TOKEN="tu_token_aqui"
BASE_URL="http://localhost:8080"

echo "=== Prueba de Endpoints: Auditoría y Ventas ==="
echo ""

# 1. Obtener todos los movimientos
echo "1. GET - Obtener todos los movimientos"
curl -X GET "$BASE_URL/api/auditoria/movimientos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
echo -e "\n\n"

# 2. Obtener movimientos por producto
echo "2. GET - Obtener movimientos por producto (productId: PROD001)"
curl -X GET "$BASE_URL/api/auditoria/producto/PROD001" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
echo -e "\n\n"

# 3. Obtener movimientos por tipo (INGRESO)
echo "3. GET - Obtener movimientos por tipo (INGRESO)"
curl -X GET "$BASE_URL/api/auditoria/tipo/INGRESO" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
echo -e "\n\n"

# 4. Obtener todas las ventas
echo "4. GET - Obtener todas las ventas"
curl -X GET "$BASE_URL/api/ventas/listar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
echo -e "\n\n"

# 5. Registrar una nueva venta
echo "5. POST - Registrar una nueva venta"
curl -X POST "$BASE_URL/api/ventas/registrar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -G \
  --data-urlencode "productId=PROD001" \
  --data-urlencode "cantidad=5" \
  --data-urlencode "precioUnitario=100.50" \
  --data-urlencode "nombreComprador=Juan Pérez" \
  --data-urlencode "telefonoComprador=123456789" \
  --data-urlencode "emailComprador=juan@example.com" \
  --data-urlencode "usuarioUsername=admin" \
  --data-urlencode "observaciones=Venta de prueba"
echo -e "\n\n"

# 6. Obtener ventas de un producto específico
echo "6. GET - Obtener ventas por producto (PROD001)"
curl -X GET "$BASE_URL/api/ventas/producto/PROD001" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
echo -e "\n\n"

# 7. Registrar un movimiento manualmente
echo "7. POST - Registrar un movimiento manualmente"
curl -X POST "$BASE_URL/api/auditoria/registrar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -G \
  --data-urlencode "productId=PROD001" \
  --data-urlencode "cantidad=10" \
  --data-urlencode "tipo=INGRESO" \
  --data-urlencode "descripcion=Compra de stock" \
  --data-urlencode "usuarioUsername=admin" \
  --data-urlencode "referencia=OC-12345"
echo -e "\n\n"

echo "=== Fin de pruebas ==="
