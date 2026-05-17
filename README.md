# Inventory Management

Repositorio monorepo con backend Spring Boot y frontend React/Vite separados por carpeta, sin cambios funcionales sobre la aplicación.

## Estructura

```text
inventory-management/
├── backend/
├── frontend/
├── config/
├── scripts/
└── .github/
```

## Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Compilación:

```powershell
cd backend
.\mvnw.cmd clean compile
```

## Frontend

```powershell
cd frontend
npm install
npm run dev
```

Build de producción:

```powershell
cd frontend
npm run build
```

## Configuración

- La configuración externa del backend sigue viviendo en `config/application.properties` en la raíz del repositorio.
- El backend ahora puede ejecutarse desde `backend/` y seguirá leyendo la carpeta `../config/`.

## Workflow de desarrollo

1. Levanta PostgreSQL con la configuración esperada en `config/application.properties`.
2. Inicia el backend desde `backend/`.
3. Inicia el frontend desde `frontend/`.
4. Accede a `http://localhost:5173`.