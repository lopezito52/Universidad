package com.universidad.servicio_cursos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.servicio_cursos.client.EstudianteClient;
import com.universidad.servicio_cursos.client.EstudianteClientDTO;
import com.universidad.servicio_cursos.dto.CursoRequestDTO;
import com.universidad.servicio_cursos.dto.InscripcionRequestDTO;
import com.universidad.servicio_cursos.repository.CursoRepository;
import feign.FeignException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests - CursoController")
class CursoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CursoRepository repository;

    // El único mock permitido en integration test:
    // Feign llama a otro servicio externo → no podemos levantar ambos en el mismo test
    @MockBean
    private EstudianteClient estudianteClient;

    @BeforeEach
    void limpiarBD() {
        repository.deleteAll();
    }

    // Helper para crear un curso y obtener su id
    private String crearCursoYObtenerID(String nombre, int capacidad) throws Exception {
        CursoRequestDTO dto = CursoRequestDTO.builder()
                .nombre(nombre)
                .descripcion("Descripción de " + nombre)
                .capacidadMaxima(capacidad)
                .build();

        String response = mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    // ── POST /api/cursos ─────────────────────────────────────

    @Test
    @DisplayName("POST /api/cursos: debe crear curso y retornar 201 con estado ACTIVO")
    void crearCurso_datosValidos_retorna201() throws Exception {
        CursoRequestDTO dto = CursoRequestDTO.builder()
                .nombre("Algoritmos")
                .descripcion("Curso de algoritmos")
                .capacidadMaxima(25)
                .build();

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Algoritmos"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.inscritos").value(0));
    }

    @Test
    @DisplayName("POST /api/cursos: debe retornar 400 cuando capacidad es 0")
    void crearCurso_capacidadCero_retorna400() throws Exception {
        CursoRequestDTO dto = CursoRequestDTO.builder()
                .nombre("Algoritmos")
                .descripcion("Curso")
                .capacidadMaxima(0)
                .build();

        mockMvc.perform(post("/api/cursos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/cursos/{id}/inscribir ──────────────────────

    @Test
    @DisplayName("POST /{id}/inscribir: debe inscribir estudiante activo y retornar 200")
    void inscribir_estudianteActivo_retorna200() throws Exception {
        String cursoId = crearCursoYObtenerID("Bases de Datos", 30);

        EstudianteClientDTO estudianteActivo = new EstudianteClientDTO(
                "est001", "Ana", "Ruiz", "ana@test.com", "ACTIVO"
        );
        when(estudianteClient.buscarPorId("est001")).thenReturn(estudianteActivo);

        InscripcionRequestDTO inscripcion = new InscripcionRequestDTO("est001");

        mockMvc.perform(post("/api/cursos/{id}/inscribir", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscripcion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inscritos").value(1))
                .andExpect(jsonPath("$.estudiantesInscritos[0].estudianteId").value("est001"));
    }

    @Test
    @DisplayName("POST /{id}/inscribir: debe retornar 400 cuando estudiante está INACTIVO")
    void inscribir_estudianteInactivo_retorna400() throws Exception {
        String cursoId = crearCursoYObtenerID("Programación", 30);

        EstudianteClientDTO estudianteInactivo = new EstudianteClientDTO(
                "est002", "Pedro", "Villa", "pedro@test.com", "INACTIVO"
        );
        when(estudianteClient.buscarPorId("est002")).thenReturn(estudianteInactivo);

        InscripcionRequestDTO inscripcion = new InscripcionRequestDTO("est002");

        mockMvc.perform(post("/api/cursos/{id}/inscribir", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscripcion)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value(containsString("est002")));
    }

    @Test
    @DisplayName("POST /{id}/inscribir: debe retornar 409 cuando el estudiante ya está inscrito")
    void inscribir_estudianteYaInscrito_retorna409() throws Exception {
        String cursoId = crearCursoYObtenerID("Redes", 30);

        EstudianteClientDTO estudiante = new EstudianteClientDTO(
                "est003", "Laura", "Vega", "laura@test.com", "ACTIVO"
        );
        when(estudianteClient.buscarPorId("est003")).thenReturn(estudiante);

        InscripcionRequestDTO inscripcion = new InscripcionRequestDTO("est003");

        // Primera inscripción
        mockMvc.perform(post("/api/cursos/{id}/inscribir", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscripcion)))
                .andExpect(status().isOk());

        // Segunda inscripción del mismo estudiante
        mockMvc.perform(post("/api/cursos/{id}/inscribir", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscripcion)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /{id}/inscribir: debe retornar 404 cuando el estudiante no existe en el otro servicio")
    void inscribir_estudianteNoExisteEnOtroServicio_retorna404() throws Exception {
        String cursoId = crearCursoYObtenerID("Historia", 30);

        when(estudianteClient.buscarPorId("noexiste"))
                .thenThrow(FeignException.NotFound.class);

        InscripcionRequestDTO inscripcion = new InscripcionRequestDTO("noexiste");

        mockMvc.perform(post("/api/cursos/{id}/inscribir", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscripcion)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /{id}/inscribir: debe retornar 503 cuando el servicio de estudiantes está caído")
    void inscribir_servicioEstudiantesCaido_retorna503() throws Exception {
        String cursoId = crearCursoYObtenerID("Matemáticas", 30);

        when(estudianteClient.buscarPorId(any()))
                .thenThrow(FeignException.ServiceUnavailable.class);

        InscripcionRequestDTO inscripcion = new InscripcionRequestDTO("est999");

        mockMvc.perform(post("/api/cursos/{id}/inscribir", cursoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inscripcion)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
    }

    // ── GET /api/cursos ──────────────────────────────────────

    @Test
    @DisplayName("GET /api/cursos: debe retornar lista de cursos activos")
    void listarActivos_conCursos_retornaLista() throws Exception {
        crearCursoYObtenerID("Física", 20);
        crearCursoYObtenerID("Química", 25);

        mockMvc.perform(get("/api/cursos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    // ── PATCH /api/cursos/{id}/desactivar ────────────────────

    @Test
    @DisplayName("PATCH /api/cursos/{id}/desactivar: debe retornar 204")
    void desactivar_cursoExistente_retorna204() throws Exception {
        String cursoId = crearCursoYObtenerID("Inglés", 15);

        mockMvc.perform(patch("/api/cursos/{id}/desactivar", cursoId))
                .andExpect(status().isNoContent());
    }
}