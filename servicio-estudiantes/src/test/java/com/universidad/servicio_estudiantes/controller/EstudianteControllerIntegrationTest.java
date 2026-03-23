package com.universidad.servicio_estudiantes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universidad.servicio_estudiantes.dto.EstudianteRequestDTO;
import com.universidad.servicio_estudiantes.repository.EstudianteRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests - EstudianteController")
class EstudianteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EstudianteRepository repository;

    // Limpia la BD embebida antes de cada test para independencia
    @BeforeEach
    void limpiarBD() {
        repository.deleteAll();
    }

    // ── POST /api/estudiantes ────────────────────────────────

    @Test
    @DisplayName("POST /api/estudiantes: debe crear estudiante y retornar 201")
    void crearEstudiante_datosValidos_retorna201() throws Exception {
        EstudianteRequestDTO dto = EstudianteRequestDTO.builder()
                .nombre("Maria")
                .apellido("Lopez")
                .email("maria@test.com")
                .fechaNacimiento(LocalDate.of(1999, 3, 20))
                .build();

        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("maria@test.com"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("POST /api/estudiantes: debe retornar 409 cuando el email ya existe")
    void crearEstudiante_emailDuplicado_retorna409() throws Exception {
        EstudianteRequestDTO dto = EstudianteRequestDTO.builder()
                .nombre("Maria")
                .apellido("Lopez")
                .email("maria@test.com")
                .fechaNacimiento(LocalDate.of(1999, 3, 20))
                .build();

        // Primer registro
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        // Segundo con el mismo email
        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value(containsString("maria@test.com")));
    }

    @Test
    @DisplayName("POST /api/estudiantes: debe retornar 400 cuando el email tiene formato inválido")
    void crearEstudiante_emailInvalido_retorna400() throws Exception {
        EstudianteRequestDTO dto = EstudianteRequestDTO.builder()
                .nombre("Maria")
                .apellido("Lopez")
                .email("no-es-un-email")
                .fechaNacimiento(LocalDate.of(1999, 3, 20))
                .build();

        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/estudiantes: debe retornar 400 cuando faltan campos obligatorios")
    void crearEstudiante_camposFaltantes_retorna400() throws Exception {
        String bodyIncompleto = "{ \"nombre\": \"Maria\" }";

        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyIncompleto))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/estudiantes/{id} ────────────────────────────

    @Test
    @DisplayName("GET /api/estudiantes/{id}: debe retornar el estudiante cuando existe")
    void buscarPorId_existente_retorna200() throws Exception {
        // Primero crea el estudiante
        EstudianteRequestDTO dto = EstudianteRequestDTO.builder()
                .nombre("Carlos")
                .apellido("García")
                .email("carlos@test.com")
                .fechaNacimiento(LocalDate.of(2001, 7, 10))
                .build();

        String response = mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // Luego lo busca por id
        mockMvc.perform(get("/api/estudiantes/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    @DisplayName("GET /api/estudiantes/{id}: debe retornar 404 cuando el id no existe")
    void buscarPorId_inexistente_retorna404() throws Exception {
        mockMvc.perform(get("/api/estudiantes/{id}", "idquenoeexiste"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── GET /api/estudiantes ─────────────────────────────────

    @Test
    @DisplayName("GET /api/estudiantes: debe retornar lista de activos")
    void listarActivos_conDatos_retornaLista() throws Exception {
        EstudianteRequestDTO dto = EstudianteRequestDTO.builder()
                .nombre("Ana")
                .apellido("Torres")
                .email("ana@test.com")
                .fechaNacimiento(LocalDate.of(2002, 1, 5))
                .build();

        mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    // ── PATCH /api/estudiantes/{id}/desactivar ───────────────

    @Test
    @DisplayName("PATCH /api/estudiantes/{id}/desactivar: debe retornar 204 y cambiar estado")
    void desactivar_estudianteExistente_retorna204() throws Exception {
        EstudianteRequestDTO dto = EstudianteRequestDTO.builder()
                .nombre("Luis")
                .apellido("Mora")
                .email("luis@test.com")
                .fechaNacimiento(LocalDate.of(1998, 9, 22))
                .build();

        String response = mockMvc.perform(post("/api/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(patch("/api/estudiantes/{id}/desactivar", id))
                .andExpect(status().isNoContent());

        // Verifica que ya no aparece en la lista de activos
        mockMvc.perform(get("/api/estudiantes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id == '" + id + "')]").doesNotExist());
    }
}