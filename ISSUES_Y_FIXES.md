# 🔧 PROBLEMAS Y SOLUCIONES ESPECÍFICAS

## ISSUE #1: VentasService - ventaDetalleRepository SIN @Autowired

### 📍 Ubicación
**Archivo**: `src/main/java/com/inventory/service/VentasService.java`  
**Línea**: 35 (donde debería estar)  
**Línea del Error**: 116

### ❌ Problema
```java
// FALTA ESTO:
@Autowired
private VentaDetalleRepository ventaDetalleRepository;

// Pero se usa aquí (línea 116):
List<VentaDetalle> detalles = ventaDetalleRepository.findByProducto(producto);
```

### ✅ Solución
**Agregar después de línea 35**:
```java
@Autowired
private VentaDetalleRepository ventaDetalleRepository;

@Autowired
private EventoProductoService eventoProductoService;  // Si se usa también
```

### 🧪 Test
```bash
curl -X GET http://localhost:8080/api/ventas/producto/PROD001 \
  -H "Authorization: Bearer $TOKEN"
# Antes: 500 NullPointerException
# Después: 200 OK con lista de ventas
```

---

## ISSUE #2: VentasController - Incompatibilidad con VentasService

### 📍 Ubicación
**Archivo Controlador**: `src/main/java/com/inventory/controller/VentasController.java`  
**Línea**: 27-36

**Archivo Servicio**: `src/main/java/com/inventory/service/VentasService.java`  
**Línea**: 41

### ❌ Problema
**Controlador espera parámetros individuales**:
```java
@PostMapping("/registrar")
public ResponseEntity<VentaDto> registrarVenta(
    @RequestParam String productId,
    @RequestParam Integer cantidad,
    @RequestParam BigDecimal precioUnitario,
    @RequestParam String nombreComprador,
    // ... 5 más parámetros
)
```

**Pero Servicio espera objeto DTO**:
```java
public VentaDto registrarVenta(VentaRegistroDto registroDto)
```

### ✅ Solución A: Modificar Controlador para usar DTO
```java
@PostMapping("/registrar")
public ResponseEntity<VentaDto> registrarVenta(
        @RequestBody VentaRegistroDto registroDto) {
    try {
        VentaDto venta = ventasService.registrarVenta(registroDto);
        return ResponseEntity.ok(venta);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null);
    }
}
```

**El cliente enviará JSON**:
```json
POST /api/ventas/registrar
Content-Type: application/json

{
  "nombreComprador": "Juan Pérez",
  "telefonoComprador": "555-1234",
  "emailComprador": "juan@example.com",
  "usuarioUsername": "admin",
  "observaciones": "Venta importante",
  "detalles": [
    {
      "productId": "PROD001",
      "cantidad": 5,
      "precioUnitario": 100.00
    },
    {
      "productId": "PROD002",
      "cantidad": 3,
      "precioUnitario": 250.50
    }
  ]
}
```

### ✅ Solución B Alternativa: Modificar Servicio (NO RECOMENDADO)
Si prefieres mantener el controlador con @RequestParam, cambiar:
```java
public VentaDto registrarVenta(
        String productId,
        Integer cantidad,
        BigDecimal precioUnitario,
        String nombreComprador,
        String telefonoComprador,
        String emailComprador,
        String usuarioUsername,
        String observaciones) {
    // ... implementación
}
```

**Nota**: Esto es menos limpio. Usa Solución A.

### 🧪 Test - Solución A
```bash
curl -X POST http://localhost:8080/api/ventas/registrar \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreComprador": "Juan Pérez",
    "telefonoComprador": "555-1234",
    "emailComprador": "juan@example.com",
    "usuarioUsername": "admin",
    "observaciones": "Prueba",
    "detalles": [
      {"productId": "PROD001", "cantidad": 5, "precioUnitario": 100}
    ]
  }'
# Antes: 400 Bad Request (parámetros no coinciden)
# Después: 200 OK con VentaDto creada
```

---

## ISSUE #3: ServiceRepairManager - Endpoint Incorrecto

### 📍 Ubicación
**Archivo**: `inventory-frontend/src/components/ServiceRepairManager.jsx`  
**Línea**: 123

### ❌ Problema
```jsx
await api.delete(`/api/servicios/${id}`);  // ❌ Incorrecto
```

**Endpoint correcto según backend**:
```
DELETE /api/servicios-reparacion/{id}  // ✅ Está en OrdenDeServicioController
```

### ✅ Solución
**Cambiar línea 123**:
```jsx
// ANTES:
await api.delete(`/api/servicios/${id}`);

// DESPUÉS:
await api.delete(`/api/servicios-reparacion/${id}`);
```

### 🧪 Test
```bash
# Antes: 404 Not Found
curl -X DELETE http://localhost:8080/api/servicios/1 \
  -H "Authorization: Bearer $TOKEN"

# Después: 200 OK
curl -X DELETE http://localhost:8080/api/servicios-reparacion/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## ISSUE #4: ApparatusManager - Endpoints Inconsistentes

### 📍 Ubicación
**Archivo**: `inventory-frontend/src/components/ApparatusManager.jsx`

### ❌ Problema
**Línea 97-98**:
```jsx
if (editingApparatus) {
  await api.put(`/api/cliente-electrodomesticos/${editingApparatus.id}`, payload);
  //                           ↑ CON 's' (INCORRECTO)
} else {
  await api.post('/api/cliente-electrodomestico/registrar', payload);
  //                           ↑ SIN 's' (CORRECTO)
}
```

**Según backend** [ClienteElectrodomesticoController](src/main/java/com/inventory/controller/ClienteElectrodomesticoController.java):
- POST: `/api/cliente-electrodomestico/registrar` (sin 's')
- PUT: `/api/cliente-electrodomestico/{id}` (sin 's')

### ✅ Solución
**Cambiar línea 97**:
```jsx
// ANTES:
await api.put(`/api/cliente-electrodomesticos/${editingApparatus.id}`, payload);
//                           ↓ Cambiar esto

// DESPUÉS:
await api.put(`/api/cliente-electrodomestico/${editingApparatus.id}`, payload);
//               Sin la 's'
```

**Además cambiar línea 131**:
```jsx
// ANTES:
await api.delete(`/api/cliente-electrodomesticos/${id}`);

// DESPUÉS:
await api.delete(`/api/cliente-electrodomestico/${id}`);
```

### 🧪 Test
```bash
# Después del FIX:
curl -X PUT http://localhost:8080/api/cliente-electrodomestico/100 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{...}'
# Resultado: 200 OK (antes era 404)
```

---

## ISSUE #5: ApparatusManager - NUNCA SE RENDERIZA (Código Muerto)

### 📍 Ubicación
**Archivo**: `inventory-frontend/src/components/Dashboard.jsx`

### ❌ Problema

**Línea 10**: Está importado
```jsx
import ApparatusManager from './ApparatusManager';
```

**Pero NUNCA aparece en renderContent()** (línea 180-226):
```jsx
const renderContent = () => {
  switch (activeModule) {
    case 'home':
      return <div>...</div>;
    case 'inventory':
      return <CrudManager ... />;
    case 'users':
      return <UserManager />;
    case 'audit':
      return <AuditModule />;
    case 'sales':
      return <SalesModule />;
    case 'clients':
      return <ClientManager />;
    case 'settings':
      return <ConfigDashboard />;
    case 'ordenes-servicio':
      return <OrdenServicio />;
    case 'ingresos':
      return <IngresoElectrodomestico />;
    // FALTA AQUÍ: case 'aparatos':
    default:
      return null;
  }
};
```

**Nota**: `IngresoElectrodomestico` (línea 233) **sí** se renderiza y maneja electrodomésticos

### ✅ Solución A: Agregar ApparatusManager a Dashboard
```jsx
// Agregar después de línea 233:
case 'aparatos':
  return <ApparatusManager />;
```

### ✅ Solución B: Eliminar ApparatusManager (REC)
Si `IngresoElectrodomestico` ya hace lo mismo, simplemente eliminar:
1. Línea 10: Quitar import
2. El archivo `ApparatusManager.jsx` si no se usa en otro lado

**Recomendación**: Revisar si ambos componentes tienen propósitos diferentes:
- **IngresoElectrodomestico**: Registrar electrodomésticos nuevos de clientes
- **ApparatusManager**: Posiblemente CRUD de electrodomésticos existentes

Si son diferentes, usar Solución A. Si son duplicados, usar Solución B.

### 🧪 Test - Solución A
```jsx
// En SideBar.jsx o similar, agregar botón:
<button onClick={() => handleModuleChange('aparatos')}>
  Aparatos
</button>
```

---

## ISSUE #6: VentasService - Método convertirADto() Incompleto

### 📍 Ubicación
**Archivo**: `src/main/java/com/inventory/service/VentasService.java`  
**Línea**: ~180

### ❌ Problema
El método `convertirADto()` está usado pero la implementación está cortada/incompleta

### ✅ Solución
**Completar método**:
```java
private VentaDto convertirADto(Venta venta) {
    List<VentaDetalleDto> detallesDto = venta.getDetalles().stream()
        .map(detalle -> new VentaDetalleDto(
            detalle.getId(),
            detalle.getProducto().getId(),
            detalle.getProducto().getName(),
            detalle.getCantidad(),
            detalle.getPrecioUnitario(),
            detalle.getSubtotal()
        ))
        .collect(Collectors.toList());
    
    return new VentaDto(
        venta.getId(),
        venta.getTotalVenta(),
        venta.getNombreComprador(),
        venta.getTelefonoComprador(),
        venta.getEmailComprador(),
        venta.getUsuario().getUsername(),
        venta.getUsuario().getFirstName() + " " + venta.getUsuario().getLastName(),
        venta.getFecha(),
        venta.getObservaciones(),
        detallesDto
    );
}
```

---

## RESUMEN DE CAMBIOS

| Sistema | Archivo | Tipo | Severidad | Línea(s) |
|---------|---------|------|-----------|----------|
| Java | VentasService.java | Agregar @Autowired | 🔴 CRÍTICO | 35 |
| Java | VentasController.java | Refactorizar método | 🔴 CRÍTICO | 27-36 |
| React | ServiceRepairManager.jsx | Cambiar endpoint | 🟡 ALTO | 123 |
| React | ApparatusManager.jsx | Corregir POST/PUT endpoints | 🟡 ALTO | 97-131 |
| React | Dashboard.jsx | Agregar renderizado o eliminar import | 🟡 ALTO | 10, 180-233 |
| Java | VentasService.java | Completar método | 🟡 ALTO | ~180 |

---

## 📋 CHECKLIST DE IMPLEMENTACIÓN

- [ ] Issue #1: Agregar @Autowired ventaDetalleRepository
- [ ] Issue #2: Cambiar VentasController para @RequestBody
- [ ] Issue #3: Corregir endpoint ServiceRepairManager
- [ ] Issue #4: Corregir endpoints ApparatusManager
- [ ] Issue #5: Decidir sobre ApparatusManager (mantener o eliminar)
- [ ] Issue #6: Completar convertirADto() en VentasService
- [ ] Test: Ejecutar todos los endpoints
- [ ] Git: Commitear cambios
- [ ] Deploy: Redeploy a producción

---

## ⏱️ TIEMPO ESTIMADO

- **Issue #1**: 2 minutos
- **Issue #2**: 10 minutos
- **Issue #3**: 1 minuto
- **Issue #4**: 2 minutos
- **Issue #5**: 5 minutos
- **Issue #6**: 5 minutos
- **Testing**: 15 minutos

**TOTAL**: ~40 minutos

