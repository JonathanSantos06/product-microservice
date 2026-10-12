# Product Microservice — Spring Boot & MongoDB

Microservicio REST para la gestión de productos, desarrollado con **Java 21, Spring Boot y MongoDB**, utilizando Docker Compose para facilitar la configuración y ejecución del entorno.

Este proyecto forma parte de mi práctica de desarrollo backend con Java, Spring Boot, bases de datos NoSQL y contenedores Docker.

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 4.1.1**
- **Spring Web** para la creación de API REST
- **Spring Data MongoDB** para el acceso a datos
- **MongoDB 8.0** como base de datos NoSQL
- **Docker y Docker Compose** para la ejecución de los servicios
- **Maven** para la gestión de dependencias
- **MongoDB Compass** para la exploración de documentos

## 📋 Características

- API REST para gestionar productos.
- Persistencia de información en MongoDB.
- Identificadores generados mediante `ObjectId`.
- Configuración de conexión mediante variables de entorno.
- Ejecución de MongoDB y la aplicación mediante Docker Compose.
- Persistencia de datos mediante volúmenes de Docker.
- Verificación de disponibilidad de MongoDB mediante un healthcheck.

## 📁 Estructura del proyecto

```text
product-microservice/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/product_microservice/
│       │       ├── entity/
│       │       │   └── ProductEntity.java
│       │       ├── repository/
│       │       │   └── ProductRepository.java
│       │       ├── controller/
│       │       └── ...
│       └── resources/
│           └── application.yaml
├── .env.example
├── .gitignore
├── Dockerfile
├── compose.yaml
├── pom.xml
└── README.md
```

> La estructura es ilustrativa. Ajusta los nombres de los paquetes y archivos a los que existan realmente en tu repositorio.

## ⚙️ Requisitos previos

Antes de ejecutar el proyecto, instala:

- Java 21, si vas a compilar o ejecutar la aplicación localmente.
- Docker Engine.
- Docker Compose v2.
- Git.

Comprueba las instalaciones:

```bash
java -version
docker --version
docker compose version
git --version
```

## 🔧 Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/product-microservice.git
cd product-microservice
```

Sustituye `TU_USUARIO` por tu usuario de GitHub y utiliza la URL real del repositorio.

### 2. Crear el archivo `.env`

En la raíz del proyecto, crea un archivo `.env` con las siguientes variables:

```dotenv
MONGO_USER=appuser
MONGO_PASSWORD=CAMBIA_ESTA_PASSWORD
```

Utiliza una contraseña propia y segura. No compartas tus credenciales ni subas el archivo `.env` a GitHub.

Si tu configuración actual utiliza otros nombres de variables, conserva los que espera tu `compose.yaml`.

### 3. Configuración de Spring Boot

Para una ejecución local, la configuración de `src/main/resources/application.yaml` puede utilizar la variable `MONGODB_URI`:

```yaml
spring:
  application:
    name: spring-mongo-demo
  mongodb:
    uri: ${MONGODB_URI}

server:
  port: 8080
```

Antes de ejecutar localmente, define `MONGODB_URI` con la dirección correspondiente a tu entorno.

Dentro de Docker, el nombre del servicio `mongodb` funciona como hostname para la conexión entre contenedores.

## 🐳 Ejecutar con Docker Compose

Construye las imágenes y levanta los servicios:

```bash
docker compose up --build -d
```

Comprueba el estado de los contenedores:

```bash
docker compose ps
```

Consulta los registros de la aplicación:

```bash
docker compose logs -f app
```

Consulta los registros de MongoDB:

```bash
docker compose logs -f mongodb
```

Para detener los servicios sin eliminar los datos:

```bash
docker compose down
```

**Importante:** evita `docker compose down -v` si quieres conservar los datos almacenados en el volumen de MongoDB.

## 🗄️ MongoDB y persistencia

La aplicación utiliza la base de datos `productdb` y la colección `product`.

| Elemento | Valor |
|---|---|
| Motor de base de datos | MongoDB 8.0 |
| Base de datos | `productdb` |
| Colección | `product` |
| Puerto publicado | `27017` |
| Volumen | `mongo_data` |

El puerto de MongoDB se publica únicamente en la interfaz local del equipo mediante `127.0.0.1:27017`.

### Visualizar los datos con MongoDB Compass

Abre MongoDB Compass y utiliza estos datos de conexión:

- Host: `localhost`
- Puerto: `27017`
- Usuario: `appuser`
- Autenticación: Username / Password
- Authentication Database: `admin`

Introduce la contraseña definida en `.env`.

Después de conectarte, navega a:

```text
productdb
└── product
```

Ahí podrás consultar los documentos guardados por la aplicación.

## 🌐 API REST

La ruta base conocida del controlador es:

```text
/api/v1/products
```

La aplicación utiliza el puerto `8080`.

### Crear un producto

Si tu controlador implementa `POST /api/v1/products`, puedes probarlo con:

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Teclado mecánico",
    "productDescription": "Teclado para desarrollo",
    "unitPrice": 1299.99
  }'
```

El cuerpo utiliza los campos de la entidad `ProductEntity`. La respuesta exacta depende de la implementación del controlador.

### Consultar productos

Si tu controlador implementa `GET /api/v1/products`:

```bash
curl http://localhost:8080/api/v1/products
```

Puedes ejecutar las peticiones desde Postman o desde la terminal y comprobar los documentos resultantes en MongoDB Compass.

> Documenta aquí los endpoints adicionales cuando estén implementados y verificados, por ejemplo, consulta por ID, actualización y eliminación.

## 🔒 Buenas prácticas de seguridad

- No guardar contraseñas en el código fuente.
- No subir el archivo `.env` al repositorio.
- Mantener un archivo `.env.example` sin credenciales reales.
- No publicar puertos de la base de datos en interfaces de red innecesarias.
- Validar los datos recibidos por la API.
- Gestionar los errores de forma consistente.
- Utilizar contraseñas seguras y revisar la configuración antes de desplegar en producción.

## 🧹 Archivo `.gitignore`

Asegúrate de incluir reglas similares a estas:

```gitignore
.env
.env.*
!.env.example

target/
.idea/
.vscode/
*.iml

.DS_Store
```

Adapta las reglas a las necesidades de tu proyecto y comprueba que no estés ignorando archivos necesarios para compilarlo.

## 🛣️ Próximas mejoras

- [ ] Completar y documentar el CRUD de productos.
- [ ] Incorporar validaciones con Jakarta Validation.
- [ ] Implementar un manejo global de excepciones.
- [ ] Agregar pruebas unitarias y de integración.
- [ ] Documentar la API con OpenAPI/Swagger.
- [ ] Configurar integración continua con GitHub Actions.
- [ ] Incorporar análisis estático y controles de seguridad.
- [ ] Preparar la configuración para un entorno de producción.

## 👨‍💻 Autor

**Jonathan Santos**

GitHub: [@JonathanSantos06](https://github.com/JonathanSantos06)

Proyecto de práctica enfocado en desarrollo backend con Java, Spring Boot, MongoDB y Docker.

---

⭐ Si este proyecto te resulta útil, puedes darle una estrella al repositorio.
