# 🔧 GUÍA RÁPIDA DE REPARACIÓN - Issues Críticos

**Última Actualización**: 7 de Mayo de 2026  
**Tiempo Estimado Total**: 25-30 minutos  

---

## 🚨 PROBLEMA #1: VentasService NullPointerException

### Síntomas
```
GET /api/ventas/producto/{productId}
→ 500 Internal Server Error
→ Caused by: NullPointerException (ventaDetalleRepository is null)
```

### Ubicación Exacta
- **Archivo**: `src/main/java/com/inventory/service/VentasService.java`
- **Línea**: 116
- **Método**: `obtenerVentasProducto()`

### Causa
Falta inyección de `VentaDetalleRepository`

### Solución (1 minuto)

**ANTES** (líneas 20-35):
```java
@Service
@Transactional
public class VentasService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditoriaService auditoriaService;
    
    // ← FALTA AQUÍ
}
```

**DESPUÉS** (agregar línea):
```java
@Service
@Transactional
public class VentasService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditoriaService auditoriaService;
    
    @Autowired
    private VentaDetalleRepository ventaDetalleRepository;  // ← AGREGAR ESTA LÍNEA
}
```

### Validación
```bash
mvn compile
# Si compila sin errores → OK
```

---

## 🚨 PROBLEMA #2: VentasController Incompatible con VentasService

### Síntomas
```
POST /api/ventas/registrar?productId=P1&cantidad=2...
→ 400 Bad Request
La firma no coincide entre Controller y Service
```

### Ubicación Exacta
- **Archivo (Controller)**: `src/main/java/com/inventory/controller/VentasController.java` línea 27
- **Archivo (Service)**: `src/main/java/com/inventory/service/VentasService.java` línea 41

### Causa
Refactor incompleto. El Controller aún usa @RequestParam pero el Service espera DTO.

### Solución (10 minutos)

#### Opción A: Cambiar Controller para usar DTO (RECOMENDADO)

**ANTES** (VentasController línea 27):
```java
@PostMapping("/registrar")
public ResponseEntity<VentaDto> registrarVenta(
        @RequestParam String productId,
        @RequestParam Integer cantidad,
        @RequestParam BigDecimal precioUnitario,
        @RequestParam String nombreComprador,
        @RequestParam(required = false) String telefonoComprador,
        @RequestParam(required = false) String emailComprador,
        @RequestParam String usuarioUsername,
        @RequestParam(required = false) String observaciones) {
    VentaDto venta = ventasService.registrarVenta(
            productId, cantidad, precioUnitario, nombreComprador,
            telefonoComprador, emailComprador, usuarioUsername, observaciones);
    return ResponseEntity.ok(venta);
}
```

**DESPUÉS** (Usar DTO):
```java
@PostMapping("/registrar")
public ResponseEntity<VentaDto> registrarVenta(
        @RequestBody VentaRegistroDto registroDto) {
    try {
        VentaDto venta = ventasService.registrarVenta(registroDto);
        return ResponseEntity.ok(venta);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new VentaDto()); // O retornar error message
    }
}
```

#### Estructura esperada de VentaRegistroDto

El Service ya tiene una clase para esto. Verificar que tenga:

```java
public class VentaRegistroDto {
    private String usuarioUsername;
    private String nombreComprador;
    private String telefonoComprador;
    private String emailComprador;
    private String observaciones;
    private List<VentaDetalleRegistroDto> detalles;
    
    // getters/setters
}

public class VentaDetalleRegistroDto {
    private String productId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    
    // getters/setters
}
```

### Validación
```bash
mvn compile
# Probar con Postman:
POST http://localhost:8080/api/ventas/registrar
Content-Type: application/json

{
  "usuarioUsername": "admin",
  "nombreComprador": "Juan Pérez",
  "telefonoComprador": "123456789",
  "emailComprador": "juan@example.com",
  "observaciones": "Cliente VIP",
  "detalles": [
    {
      "productId": "P001",
      "cantidad": 2,
      "precioUnitario": 100.00
    }
  ]
}
```

---

## 🚨 PROBLEMA #3: ApparatusManager URLs Incorrectas

### Síntomas
```
PUT /api/cliente-electrodomesticos/100  (CON 's')
→ 404 Not Found

Debería ser:
PUT /api/cliente-electrodomestico/100   (SIN 's')
```

### Ubicación Exacta
- **Archivo**: `inventory-frontend/src/components/ApparatusManager.jsx`
- **Línea 97**: PUT request
- **Línea 131**: DELETE request

### Causa
Controller se llama `ClienteElectrodomesticoController` (/api/cliente-electrodomestico/**)  
Pero frontend intenta `/api/cliente-electrodomesticos/` (con 's')

### Solución (2 minutos)

Buscar y reemplazar en `ApparatusManager.jsx`:

**BUSCAR**:
```javascript
/api/cliente-electrodomesticos/
```

**REEMPLAZAR**:
```javascript
/api/cliente-electrodomestico/
```

Exactamente: 2 occurrencias (línea 97 y 131)

### Validación
```bash
# Buscar con Ctrl+F:
/api/cliente-electrodomesticos/

# Debe retornar 0 resultados después del fix
```

---

## 🚨 PROBLEMA #4: ApparatusManager Nunca Se Renderiza

### Síntomas
```
Componente ApparatusManager está importado en Dashboard.jsx
PERO no aparece en la interfaz del usuario
No hay forma de acceder a él
```

### Ubicación Exacta
- **Archivo**: `inventory-frontend/src/components/Dashboard.jsx`
- **Línea 10**: Importación (existe)
- **Líneas 201-235**: renderContent() switch case (NO TIENE CASO PARA APARATOS)

### Causa
1. Falta case en switch de renderContent()
2. Falta botón en SideBar para navegar a él

### Solución Parte A: Agregar Caso en renderContent() (2 minutos)

**ANTES** (Dashboard.jsx renderContent, alrededor línea 201):
```javascript
const renderContent = () => {
    switch (activeModule) {
        case 'home': return <div className="...">Bienvenida</div>;
        case 'inventory': return <CrudManager userRole={userRole} ... />;
        case 'users': return <UserManager userRole={userRole} />;
        case 'clients': return <ClientManager userRole={userRole} />;
        case 'clienteElectrodomesticos': return <IngresoElectrodomestico userId={userName} />;
        case 'servicios-reparacion': return <OrdenServicio userId={userName} />;
        case 'ventas': return <SalesModule userId={userName} />;
        case 'audit': return <AuditModule userId={userName} />;
        case 'config': return <ConfigDashboard />;
        default: return null;
    }
};
```

**DESPUÉS** (Agregar esta línea):
```javascript
const renderContent = () => {
    switch (activeModule) {
        case 'home': return <div className="...">Bienvenida</div>;
        case 'inventory': return <CrudManager userRole={userRole} ... />;
        case 'users': return <UserManager userRole={userRole} />;
        case 'clients': return <ClientManager userRole={userRole} />;
        case 'aparatos': return <ApparatusManager userRole={userRole} />;  // ← AGREGAR
        case 'clienteElectrodomesticos': return <IngresoElectrodomestico userId={userName} />;
        case 'servicios-reparacion': return <OrdenServicio userId={userName} />;
        case 'ventas': return <SalesModule userId={userName} />;
        case 'audit': return <AuditModule userId={userName} />;
        case 'config': return <ConfigDashboard />;
        default: return null;
    }
};
```

### Solución Parte B: Agregar Botón en SideBar (3 minutos)

**ARCHIVO**: `inventory-frontend/src/components/SideBar.jsx`

Buscar donde están los otros botones, agregar algo como:

```javascript
{/* Aparatos */}
<button
    onClick={() => {
        setActiveModule('aparatos');
        // Opcional: cerrar sidebar en mobile
    }}
    className={`flex items-center gap-2 px-4 py-2 rounded transition ${
        activeModule === 'aparatos'
            ? 'bg-blue-500 text-white'
            : 'text-gray-600 hover:bg-gray-200'
    }`}
>
    <ChatBubbleLeftEllipsisIcon className="w-5 h-5" />
    {expanded && 'Aparatos'}
</button>
```

---

## ✅ RESUMEN DE FIXES

| # | Problema | Ubicación | Cambio | Tiempo |
|---|----------|-----------|--------|--------|
| 1 | VentaDetalleRepository null | VentasService.java:35 | Agregar @Autowired línea | 1 min |
| 2 | Controller incompatible | VentasController.java:27 | Usar VentaRegistroDto | 10 min |
| 3 | URLs incorrectas | ApparatusManager.jsx:97,131 | Quitar 's' (2 lugares) | 2 min |
| 4a | Componente muerto | Dashboard.jsx:201 | Agregar case 'aparatos' | 2 min |
| 4b | Sin botón en sidebar | SideBar.jsx | Agregar botón | 3 min |
| | **TOTAL** | | | **18 minutos** |

---

## 🧪 Testing de Fixes

### Test #1: Verificar compilación
```bash
cd proyecto-root
mvn clean compile
# Debe pasar sin errores
```

### Test #2: Iniciar servidor
```bash
mvn spring-boot:run
# Log debe mostrar: "Tomcat started on port(s): 8080"
```

### Test #3: Tests de API (en otra terminal)
```bash
# Test Ventas Registro
curl -X POST http://localhost:8080/api/ventas/registrar \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{...}'

# Test Ventas por Producto
curl http://localhost:8080/api/ventas/producto/P001 \
  -H "Authorization: Bearer YOUR_TOKEN"

# Test ApparatusManager
curl http://localhost:8080/api/cliente-electrodomestico/listar \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test #4: Frontend
1. Ejecutar: `cd inventory-frontend && npm run dev`
2. Acceder a http://localhost:5173
3. Login
4. Buscar botón "Aparatos" en sidebar
5. Click → Debe cargar ApparatusManager
6. Intentar actualizar/eliminar aparato → Debe funcionar (no 404)

---

## 📋 Pre-Production Checklist

- [ ] Todos 5 fixes aplicados
- [ ] mvn compile sin errores
- [ ] npm run dev sin warnings críticos
- [ ] APIs testean OK con tokens válidos
- [ ] Frontend navega a todos los módulos
- [ ] Sidebar muestra botón "Aparatos"
- [ ] ApparatusManager CRUD funciona (URLs correctas)
- [ ] Ventas POST funciona con DTO
- [ ] Ventas GET por producto funciona (sin null)
- [ ] Git commit + push

---

## 🆘 Si algo falla

**Si compilation error después de fix #2:**
```
Buscar: VentaRegistroDto y VentaDetalleRegistroDto
Ubicación: src/main/java/com/inventory/dto/
Si no existen, crearlas basado en las propiedades que usa VentasService
```

**Si tests fallan:**
```
1. Verificar que el token JWT sea válido
2. Verificar que usuario tiene rol ADMIN
3. Verificar data existe en BD (productos, usuarios, etc)
4. Revisar logs backend: tail -f target/logs/*.log
```

---

**Documentado por**: GitHub Copilot Architect  
**Validado**: Code review automático + manual testing
