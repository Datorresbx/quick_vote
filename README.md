# Quick Vote

Aplicación Spring Boot 3 (Thymeleaf + REST) para una votación rápida con 4 opciones fijas: Michoacán, Oaxaca, Jalisco y Guanajuato. Pensada para 200–300 personas usando una sola página móvil con botones grandes y resultados en vivo (polling cada 1s). Incluye panel de administración con autenticación básica y orquestación con Docker Compose (app + PostgreSQL).

## Características
- Votación móvil con 4 opciones.
- Ventana de tiempo configurable para aceptar votos (iniciar/detener desde Admin).
- Votos ilimitados por persona mientras la sesión esté abierta.
- Resultados en vivo y resultado final por sesión de votación.
- Seguridad con HTTP Basic para el panel y APIs de administración.
- Persistencia en PostgreSQL; esquema autogenerado (Hibernate ddl-auto=update).

## Requisitos
- Java 21+ (el proyecto usa toolchain Java 21 para compilar y Docker usa JDK 21)
- Docker y Docker Compose (recomendado para pruebas rápidas)
- Alternativamente, para ejecutar localmente sin Docker para la app: Gradle Wrapper incluido
  - Windows: `gradlew.bat`
  - Linux/macOS: `./gradlew`

## Estructura rápida
- UI pública (Thymeleaf): `GET /`
- Panel Admin (HTTP Basic): `GET /admin`
- API pública:
  - `POST /api/vote/{option}` (MICHOACAN|OAXACA|JALISCO|GUANAJUATO)
  - `GET /api/results`
  - `GET /api/session-status`
- API Admin (HTTP Basic):
  - `POST /api/admin/start?seconds=60`
  - `POST /api/admin/stop`

Credenciales por defecto del Admin: `admin` / `admin123` (cámbialas con variables de entorno).

---

## 1) Ejecutar con Docker Compose (recomendado)
1. Construir e iniciar servicios:
   ```bash
   docker compose build
   docker compose up -d
   ```
2. Abrir en el navegador:
   - App pública: http://localhost:8080/
   - Panel Admin: http://localhost:8080/admin
     - Usuario: `admin`
     - Contraseña: `admin123`

El archivo `compose.yaml` ya define:
- PostgreSQL (puerto 5432, db `mydatabase`, user `myuser`, pass `secret`).
- App en puerto 8080 con variables de entorno para base de datos y credenciales.

Para ver logs:
```bash
docker compose logs -f app
```
Para detener:
```bash
docker compose down
```

## 2) Ejecutar localmente (app con Gradle, DB en Docker)
1. Levantar solo PostgreSQL con Docker:
   ```bash
   docker compose up -d postgres
   ```
2. Exportar variables de entorno (opcional; por defecto el `application.properties` ya coincide con `compose`):
   - En Windows PowerShell:
     ```powershell
     $Env:PORT="8080"
     $Env:DB_URL="jdbc:postgresql://localhost:5432/mydatabase"
     $Env:DB_USERNAME="myuser"
     $Env:DB_PASSWORD="secret"
     $Env:ADMIN_USERNAME="admin"
     $Env:ADMIN_PASSWORD="admin123"
     ```
3. Iniciar la aplicación:
   - Windows:
     ```bat
     gradlew.bat bootRun
     ```
   - Linux/macOS:
     ```bash
     ./gradlew bootRun
     ```
4. Navega a http://localhost:8080/ y http://localhost:8080/admin

> Nota: si usas un PostgreSQL local distinto, ajusta `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.

## 3) Probar rápidamente con curl
- Enviar un voto a Jalisco:
  ```bash
  curl -X POST http://localhost:8080/api/vote/JALISCO
  ```
- Consultar resultados actuales:
  ```bash
  curl http://localhost:8080/api/results
  ```
- Ver estado de la sesión (abierta/cerrada y segundos restantes):
  ```bash
  curl http://localhost:8080/api/session-status
  ```
- Iniciar una votación por 60s (requiere auth básica):
  ```bash
  curl -X POST "http://localhost:8080/api/admin/start?seconds=60" -u admin:admin123
  ```
- Detener la votación (requiere auth básica):
  ```bash
  curl -X POST http://localhost:8080/api/admin/stop -u admin:admin123
  ```

Opciones válidas para votar: `MICHOACAN`, `OAXACA`, `JALISCO`, `GUANAJUATO`.

## 4) Variables de entorno soportadas
- `PORT` (por defecto 8080)
- `DB_URL` (por defecto `jdbc:postgresql://localhost:5432/mydatabase`)
- `DB_USERNAME` (por defecto `myuser`)
- `DB_PASSWORD` (por defecto `secret`)
- `ADMIN_USERNAME` (por defecto `admin`)
- `ADMIN_PASSWORD` (por defecto `admin123`)

Estas variables ya están mapeadas en `application.properties`.

## 5) Ejecutar pruebas
- Con Gradle Wrapper:
  - Windows:
    ```bat
    gradlew.bat test
    ```
  - Linux/macOS:
    ```bash
    ./gradlew test
    ```

## 6) Solución de problemas
- La app no arranca por error de base de datos:
  - Asegúrate de que PostgreSQL está arriba y accesible (puerto 5432). Con Compose: `docker compose ps`.
  - Verifica que `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` coincidan con tu instancia.
- Puerto 8080 ocupado:
  - Cambia `PORT` a otro valor (p. ej. 8081) y vuelve a levantar la app/compose.
- Credenciales Admin no funcionan:
  - Si las cambiaste vía env, reinicia la app para que tomen efecto.

## Licencia
Uso educativo/demostrativo. Ajusta según tus necesidades antes de producción.