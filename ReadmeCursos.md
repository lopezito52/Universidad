#Universidad
# Servicio Cursos

Microservicio REST para la gestión de cursos e inscripciones de una universidad.
Desarrollado con Spring Boot 3.2, MongoDB Atlas y OpenFeign.

## Tecnologías
- Java 17
- Spring Boot 3.2
- Spring Data MongoDB
- Spring Cloud OpenFeign
- MongoDB Atlas
- Lombok
- Bean Validation

## Requisitos previos
- Java 17+
- Maven 3.8+
- Cuenta en MongoDB Atlas con cluster configurado
- `servicio-estudiantes` corriendo en `localhost:8081`

## Configuración

En `src/main/resources/application.properties`:
```properties
spring.data.mongodb.uri=mongodb+srv://USUARIO:PASSWORD@cluster.mongodb.net/universidad_cursos
estudiantes.service.url=http://localhost:8081
```

## Levantar el servicio
```bash
# Primero levantar servicio-estudiantes, luego:
mvn spring-boot:run
```

El servicio queda disponible en `http://localhost:8082`

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/cursos` | Crear nuevo curso |
| GET | `/api/cursos` | Listar cursos activos |
| GET | `/api/cursos/{id}` | Buscar por ID |
| POST | `/api/cursos/{id}/inscribir` | Inscribir estudiante |
| PATCH | `/api/cursos/{id}/desactivar` | Desactivar curso |

## Ejemplo de request
```json
POST /api/cursos/{id}/inscribir
{
  "estudianteId": "abc123"
}
```

## Reglas de negocio
- No se puede inscribir a un estudiante INACTIVO
- No se puede inscribir a un estudiante ya inscrito en el mismo curso
- El curso cambia a estado LLENO al alcanzar su capacidad máxima
- Al inscribir, se consulta `servicio-estudiantes` via REST para validar el estudiante

## Casos de uso
- Crear un nuevo curso
- Inscribir un estudiante a un curso (llama al módulo Estudiantes vía REST para validar que existe y está ACTIVO)
- Listar estudiantes inscritos en un curso
- Consultar curso por ID
- Desactivar un curso

## Comunicación entre servicios
```
servicio-cursos (8082) ──GET /api/estudiantes/{id}──► servicio-estudiantes (8081)
```

## Tests
```bash
# Unit tests
mvn test -Dtest=CursoServiceTest

# Integration tests
mvn test -Dtest=CursoControllerIT

# Todos los tests
mvn test
```
