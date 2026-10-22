# TiendaSpring

## Docker

Construye y ejecuta la imagen localmente:

```powershell
docker build -t bodegaweb:local .
docker run -d --name bodegaweb -p 8080:8080 `
  -e MYSQL_URL="jdbc:mysql://<host>:3306/bodegaweb?createDatabaseIfNotExist=true" `
  -e MYSQL_USER="<usuario>" `
  -e MYSQL_PASSWORD="<contraseña>" `
  -e JWT_SECRET="<clave-de-al-menos-32-caracteres>" `
  bodegaweb:local
```

También puedes levantar MySQL y la aplicación con `docker-compose.yml`:

```powershell
docker compose up -d
```

MySQL queda disponible en el puerto `3307` del host y la aplicación en el `8080`. Cambia `JWT_SECRET` y `SEED_ADMIN_PASSWORD` en el compose antes de usarlo fuera de desarrollo.

El workflow `.github/workflows/docker-image.yml` construye y publica la imagen en GHCR. Al hacer push a `main`, también la despliega con Docker en un runner de GitHub Actions autohospedado. Ese runner debe ser Windows, tener Docker Engine disponible y poder conectarse a MySQL desde el contenedor.

Configura estos secretos del repositorio para habilitar el despliegue:

- `MYSQL_URL`: URL JDBC de MySQL accesible desde el contenedor.
- `MYSQL_USER` y `MYSQL_PASSWORD`: credenciales de la base de datos.
- `JWT_SECRET`: clave de al menos 32 caracteres para firmar los tokens.

La aplicación queda publicada en el puerto `8080` del host del runner.
