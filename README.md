# TiendaSpring

## Despliegue local con Docker

### Requisitos

- Docker Desktop instalado y en ejecución, con Docker Compose v2 (`docker compose`).
- Git para clonar el repositorio.

### Iniciar la aplicación

Clona el repositorio y entra en su directorio:

```sh
git clone https://github.com/ShyGabriel/TiendaSpring.git
cd TiendaSpring
```

Crea tu archivo local de configuración a partir de la plantilla. En Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

En macOS/Linux:

```sh
cp .env.example .env
```

Los valores del ejemplo son **solo para desarrollo local**. Cambia `JWT_SECRET` por una clave aleatoria de al menos 32 caracteres y `SEED_ADMIN_PASSWORD` antes de iniciar:

```sh
docker compose up --build -d
```

La primera ejecución descarga MySQL y construye la imagen de la aplicación. Espera a que ambos servicios estén listos:

```sh
docker compose ps
docker compose logs -f app
```

La aplicación queda en [http://localhost:8080](http://localhost:8080), Swagger en [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) y la especificación OpenAPI en [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs). MySQL se publica en el puerto `3307` del equipo.

La migración crea el usuario inicial `admin@bodegaweb.com`; su contraseña es el valor de `SEED_ADMIN_PASSWORD` configurado **la primera vez** que se crea la base.

### Detener y volver a iniciar

```sh
docker compose down
docker compose up -d
```

Los datos de MySQL se conservan en un volumen de Docker al detener los contenedores. **No ejecutes `docker compose down -v` salvo que quieras borrar la base local.** Si cambias la contraseña de MySQL después de inicializar el volumen, actualiza también las credenciales dentro de MySQL: las variables de Compose solo configuran las credenciales al crear una base nueva.

Si los puertos `8080` o `3307` ya están ocupados, modifica `APP_PORT` o `MYSQL_PORT` en `.env`. Por ejemplo, con `APP_PORT=8081`, la aplicación se abrirá en `http://localhost:8081`.

### Diagnóstico

- `docker compose ps`: estado y puertos de los servicios.
- `docker compose logs -f app`: errores de arranque de Spring.
- `docker compose logs -f db`: estado de MySQL.
- Confirma que Docker Desktop esté iniciado y que las claves de `.env` no se hayan cambiado después de crear el volumen de MySQL.

### Publicación automática en GitHub

El workflow `.github/workflows/docker-image.yml` construye y publica la imagen en GHCR. En cada push a `main`, intenta desplegarla además en un runner de GitHub Actions autohospedado. Ese runner requiere Docker y acceso a una instancia MySQL externa. Configura `MYSQL_URL`, `MYSQL_USER`, `MYSQL_PASSWORD` y `JWT_SECRET` como secretos del repositorio; la clave JWT debe tener al menos 32 caracteres. `MYSQL_USER` debe ser una cuenta MySQL con permisos sobre la base y acceso desde el runner/contenedor; evita usar `root`, que normalmente no acepta conexiones remotas.

También puedes iniciarlo manualmente desde **Actions → Build and Push Docker Image → Run workflow**, seleccionando la rama `main`. El despliegue manual en `main` usa los mismos secretos del repositorio.

El contenedor publicado por este workflow escucha en el puerto `8081` del runner (puerto interno `8080`). Antes de iniciarlo, el workflow elimina los contenedores Docker que publiquen el puerto `8080` del host; no uses ese puerto para otros contenedores que necesites conservar. La app desplegada por Actions queda en `http://<host-del-runner>:8081`.
