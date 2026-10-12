# Product Microservice — Spring Boot & MongoDB

Microservicio REST para la gestión de productos, desarrollado con Java 21, Spring Boot y MongoDB. Docker Compose permite levantar la aplicación y la base de datos en un entorno local.

## Tecnologías

- Java 21
- Spring Boot
- Spring Web
- Spring Data MongoDB
- MongoDB 8.0
- Docker Engine y Docker Compose
- Maven
- MongoDB Compass para inspeccionar documentos

## Funcionalidad actual

- API REST con ruta base `/api/v1/products`.
- Persistencia de productos en MongoDB.
- Uso de Docker para ejecutar aplicación y base de datos.
- Volumen de Docker para conservar los datos de MongoDB.
- Healthcheck de MongoDB para coordinar el arranque de los servicios.

> Los endpoints disponibles dependen de los métodos implementados en el controlador. Consulta el código para verificar las operaciones soportadas.

## Requisitos

- Git
- Docker Engine
- Docker Compose v2
- Java 21 y Maven solo si deseas ejecutar o compilar la aplicación fuera de Docker.

Comprueba la instalación:

```bash
java -version
docker --version
docker compose version
git --version
```

## Clonar el repositorio

```bash
git clone https://github.com/JonathanSantos06/product-microservice.git
cd product-microservice
```

## Configuración de variables de entorno

Crea un archivo local `.env` en la raíz del proyecto. El ejemplo siguiente corresponde a los nombres de variables utilizados por el Docker Compose de este proyecto:

```dotenv
MONGO_USER=appuser
MONGO_PASSWORD=CAMBIA_ESTA_PASSWORD
```

Utiliza una contraseña segura. No subas el archivo `.env` ni credenciales reales a GitHub. Puedes incluir un archivo `.env.example` sin secretos como plantilla.

## Ejecutar con Docker Compose

Construye las imágenes e inicia los contenedores:

```bash
docker compose up --build -d
```

Consulta el estado de los servicios:

```bash
docker compose ps
```

Consulta los logs de la aplicación:

```bash
docker compose logs -f app
```

Consulta los logs de MongoDB:

```bash
docker compose logs -f mongodb
```

Para detener los contenedores sin eliminar el volumen de datos:

```bash
docker compose down
```

**Importante:** evita `docker compose down -v` si necesitas conservar los documentos de MongoDB, ya que elimina los volúmenes declarados para el proyecto.

## MongoDB y MongoDB Compass

Configuración esperada en el entorno local:

| Propiedad | Valor |
|---|---|
| Base de datos | `productdb` |
| Colección | `product` |
| Puerto publicado | `27017` |
| Volumen | `mongo_data` |

Para conectarte con MongoDB Compass, usa:

- Host: `localhost`
- Puerto: `27017`
- Usuario: el valor de `MONGO_USER`
- Método de autenticación: Username / Password
- Authentication Database: `admin`
- Contraseña: el valor de `MONGO_PASSWORD`

Dentro de Docker, la aplicación se conecta a MongoDB mediante el nombre del servicio `mongodb`, no mediante `localhost`. La URI debe apuntar a la base `productdb` y usar `authSource=admin` si las credenciales corresponden al usuario administrador creado por la imagen oficial.

La base de datos y la colección pueden aparecer después de que la aplicación inserte el primer documento.

## API REST

La ruta base del controlador es:

```text
http://localhost:8080/api/v1/products
```

Si el controlador tiene implementado `POST /api/v1/products`, puedes probar la creación de un producto con:

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Teclado mecánico",
    "productDescription": "Teclado para desarrollo",
    "unitPrice": 1299.99
  }'
```

El JSON usa los nombres de campos vistos en el documento de ejemplo guardado en MongoDB. Confirma en el controlador qué métodos HTTP están implementados y cuál es el formato de respuesta real.

## Configuración de Spring Boot

Para ejecución local, la configuración puede utilizar una variable de entorno para no escribir credenciales en el archivo YAML:

```yaml
spring:
  application:
    name: spring-mongo-demo
  mongodb:
    uri: ${MONGODB_URI}

server:
  port: 8080
```

Asegúrate de que la variable de conexión corresponda al nombre de propiedad que admite la versión de Spring Boot del proyecto. En Docker, configura la URI para conectar a `mongodb:27017`.

## Seguridad

- No subir `.env` ni secretos al repositorio.
- Mantener credenciales fuera del código fuente.
- No publicar MongoDB en una interfaz de red pública sin una razón y controles adecuados.
- Validar datos de entrada y manejar errores de forma consistente.
- Revisar la configuración de seguridad antes de desplegar en producción.

## Próximas mejoras

- [ ] Completar y documentar todas las operaciones CRUD.
- [ ] Añadir validaciones de entrada.
- [ ] Implementar manejo global de excepciones.
- [ ] Agregar pruebas unitarias y de integración.
- [ ] Documentar endpoints con OpenAPI/Swagger.
- [ ] Configurar GitHub Actions para compilar y ejecutar pruebas.

## Autor

**Jonathan Santos**

GitHub: [@JonathanSantos06](https://github.com/JonathanSantos06)

---

Proyecto de práctica para desarrollar habilidades backend con Java, Spring Boot, MongoDB y Docker.
