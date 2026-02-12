# Copilot / AI Agent Instructions for this repository

Purpose: Quickly orient an AI coding agent to be productive editing and extending this full-stack Spring Boot + React app.

- **Big picture:** Backend is a Spring Boot (Java 17) monolith under `src/main/java/com/inventory/` (common packages: `controller`, `service`, `repository`, `dto`, `model`, `config`, `util`). Frontend is a Vite + React app in `inventory-frontend/` (entry: `inventory-frontend/src/main.jsx`, components in `inventory-frontend/src/components/`).

- **How to run (dev):**
  - Backend: use Maven wrapper. On Windows run `mvnw.cmd spring-boot:run` from repo root (or `./mvnw spring-boot:run` on *nix). Java 17 is required (see `pom.xml`).
  - Frontend: `cd inventory-frontend && npm install && npm run dev` (Vite default port 5173).

- **Build / package:**
  - Backend: `mvnw.cmd package` → runnable jar under `target/`.
  - Frontend: `cd inventory-frontend && npm run build` → production assets.

- **API surface & dataflow highlights:**
  - Frontend axios base URL is `http://localhost:8080` (see `inventory-frontend/src/components/utils/axiosConfig.jsx`). The axios instance automatically adds the JWT from `localStorage.authToken` and redirects to `/login` on 401/403.
  - Key backend endpoints:
    - Auth: `/auth/login`, `/auth/register` (public)
    - Products: `/api/products/*` — examples: `/api/products/listar`, `/api/products/agregar`, `/api/products/actualizar/{id}`, `/api/products/eliminar`.
  - Security: `src/main/java/com/inventory/config/SecurityConfig.java` enforces JWT-based stateless auth and requires `ROLE_ADMIN` for `/api/products/**` and `/api/categories/**`.

- **Important files to inspect when changing behavior:**
  - Backend app entry: `src/main/java/com/inventory/InventoryManagementApplication.java`
  - Security + CORS: `src/main/java/com/inventory/config/SecurityConfig.java`, `WebConfig.java` (CORS allows `http://localhost:5173`).
  - Controllers: `src/main/java/com/inventory/controller/*` (e.g., `ProductController.java`).
  - Service layer: `src/main/java/com/inventory/service/*` (business logic, use this to add validations/transactions).
  - DTOs: `src/main/java/com/inventory/dto/*` — controllers accept DTOs; services often convert to entities.
  - Frontend axios helper: `inventory-frontend/src/components/utils/axiosConfig.jsx` (update if backend base URL or token key changes).

- **Conventions & patterns (project-specific):**
  - Spanish/English mix in identifiers: e.g., `ProductoService`, `CategoriaDeProductosService`, `ProductDto` — search both variants when making changes.
  - Controllers expect DTOs and rely on service-layer conversion (look for `toProducto`/constructor patterns in DTO classes).
  - JWT config: `jwt.secret` and `jwt.expiration` live in `src/main/resources/application.properties`; production secrets should be moved to environment variables.
  - DB: PostgreSQL is the expected runtime DB (see `application.properties`), and `spring.jpa.hibernate.ddl-auto=update` is enabled (be careful with schema changes).

- **Dependencies & integration points:**
  - JWT: `io.jsonwebtoken:jjwt-*` (see `pom.xml`) and a custom `util/JwtFilter` for request filtering.
  - Passwords: BCrypt via `BCryptPasswordEncoder` bean in `SecurityConfig.java`.
  - Frontend: React 19 + Vite; routing via `react-router-dom`; axios instance used for all API calls.

- **Testing & developer workflows:**
  - Backend tests: `mvnw.cmd test`.
  - Frontend lint: from `inventory-frontend`: `npm run lint`.
  - To debug auth issues: check `spring.jpa.show-sql=true` and backend logs; frontend logs axios request/response in `axiosConfig.jsx`.

- **Quick examples:**
  - List products (curl):
    ```bash
    curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/products/listar
    ```
  - Run backend (Windows):
    ```powershell
    mvnw.cmd spring-boot:run
    ```
  - Run frontend dev server:
    ```bash
    cd inventory-frontend
    npm install
    npm run dev
    ```

- **Editing guidance for AI agents:**
  - Preserve DTO -> entity conversion logic and service-layer responsibilities. Prefer adding logic in `service/` rather than controllers.
  - When modifying endpoint paths or CORS, update both `SecurityConfig.java`/`WebConfig.java` and `axiosConfig.jsx`.
  - Avoid committing secrets found in `application.properties`; prefer environment-based overrides.
  - Watch naming inconsistencies (Spanish vs English) when searching/refactoring to avoid missed references.

If any section is unclear or you'd like me to expand with concrete code-editing examples (tests, refactors, or a CI workflow), tell me which area to expand.
