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

### Módulo Estudiantes:

 - Un estudiante debe tener nombre, apellido, email único y fecha de nacimiento
- No se puede registrar un estudiante con un email ya existente
- Un estudiante puede estar en estado ACTIVO o INACTIVO
- No se puede eliminar un estudiante que tenga cursos asignados

### Módulo Cursos:

- Un curso debe tener nombre, descripción, capacidad máxima y estado
- Un curso puede estar en estado ACTIVO, INACTIVO o LLENO
- No se pueden inscribir más estudiantes de la capacidad máxima
- No se puede inscribir un estudiante INACTIVO a un curso
- No se puede inscribir un estudiante que ya está en el curso


## Casos de Uso
### Módulo Estudiantes:

- Registrar un nuevo estudiante
- Actualizar datos de un estudiante
- Consultar estudiante por ID o por email
- Listar todos los estudiantes activos
- Desactivar un estudiante

### Módulo Cursos:

- Crear un nuevo curso
- Inscribir un estudiante a un curso (llama al módulo Estudiantes vía REST para validar que existe y está ACTIVO)
- Listar estudiantes inscritos en un curso
- Consultar curso por ID
- Desactivar un curso

## Tests
```bash
# Unit tests
mvn test -Dtest=EstudianteServiceTest

# Integration tests
mvn test -Dtest=EstudianteControllerIT

# Todos los tests
mvn test
```
