# Universidad
# Servicio Estudiantes
## Integrantes
- Samuel Acero
- Nicolas Urrea
- Samuel López

Microservicio REST para la gestión de estudiantes de una universidad.
Desarrollado con Spring Boot 3.2 y MongoDB Atlas.

## Tecnologías
- Java 17
- Spring Boot 3.2
- Spring Data MongoDB
- MongoDB Atlas
- Lombok
- Bean Validation

## Requisitos previos
- Java 17+
- Maven 3.8+
- Cuenta en MongoDB Atlas con cluster configurado

## Configuración

En `src/main/resources/application.properties` reemplaza la URI de Atlas:
```properties
spring.data.mongodb.uri=mongodb+srv://USUARIO:PASSWORD@cluster.mongodb.net/universidad_estudiantes
```

## Levantar el servicio
```bash
mvn spring-boot:run
```

El servicio queda disponible en `http://localhost:8081`

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/estudiantes` | Registrar nuevo estudiante |
| GET | `/api/estudiantes` | Listar estudiantes activos |
| GET | `/api/estudiantes/{id}` | Buscar por ID |
| GET | `/api/estudiantes/email/{email}` | Buscar por email |
| PUT | `/api/estudiantes/{id}` | Actualizar estudiante |
| PATCH | `/api/estudiantes/{id}/desactivar` | Desactivar estudiante |

## Ejemplo de request
```json
POST /api/estudiantes
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@universidad.com",
  "fechaNacimiento": "2000-05-15"
}
```

## Reglas de negocio
- El email debe ser único en el sistema
- Un estudiante se crea con estado ACTIVO por defecto
- Un estudiante INACTIVO no puede inscribirse en cursos

## Tests
```bash
# Unit tests
mvn test -Dtest=EstudianteServiceTest

# Integration tests
mvn test -Dtest=EstudianteControllerIT

# Todos los tests
mvn test
```
