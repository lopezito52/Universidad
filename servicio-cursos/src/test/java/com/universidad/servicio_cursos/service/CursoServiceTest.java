package com.universidad.servicio_cursos.service;

import com.universidad.servicio_cursos.client.EstudianteClient;
import com.universidad.servicio_cursos.client.EstudianteClientDTO;
import com.universidad.servicio_cursos.dto.CursoRequestDTO;
import com.universidad.servicio_cursos.dto.CursoResponseDTO;
import com.universidad.servicio_cursos.dto.InscripcionRequestDTO;
import com.universidad.servicio_cursos.exception.*;
import com.universidad.servicio_cursos.model.Curso;
import com.universidad.servicio_cursos.model.Curso.EstadoCurso;
import com.universidad.servicio_cursos.model.EstudianteInscrito;
import com.universidad.servicio_cursos.repository.CursoRepository;
import feign.FeignException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - CursoService")
class CursoServiceTest {

    @Mock
    private CursoRepository repository;

    @Mock
    private EstudianteClient estudianteClient;

    @InjectMocks
    private CursoServiceImpl service;

    private Curso cursoActivo;
    private CursoRequestDTO cursoRequestDTO;
    private EstudianteClientDTO estudianteActivo;
    private InscripcionRequestDTO inscripcionDTO;

    @BeforeEach
    void setUp() {
        cursoActivo = Curso.builder()
                .id("curso001")
                .nombre("Introducción a Java")
                .descripcion("Curso básico")
                .capacidadMaxima(30)
                .estado(EstadoCurso.ACTIVO)
                .estudiantesInscritos(new ArrayList<>())
                .creadoEn(LocalDateTime.now())
                .build();

        cursoRequestDTO = CursoRequestDTO.builder()
                .nombre("Introducción a Java")
                .descripcion("Curso básico")
                .capacidadMaxima(30)
                .build();

        estudianteActivo = new EstudianteClientDTO(
                "est001", "Juan", "Pérez", "juan@test.com", "ACTIVO"
        );

        inscripcionDTO = new InscripcionRequestDTO("est001");
    }

    // ── crear ────────────────────────────────────────────────

    @Test
    @DisplayName("crear: debe guardar el curso y retornar DTO con estado ACTIVO")
    void crear_datosValidos_retornaCursoActivo() {
        when(repository.save(any(Curso.class))).thenReturn(cursoActivo);

        CursoResponseDTO resultado = service.crear(cursoRequestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoCurso.ACTIVO);
        assertThat(resultado.getNombre()).isEqualTo("Introducción a Java");
        verify(repository, times(1)).save(any(Curso.class));
    }

    // ── inscribirEstudiante ──────────────────────────────────

    @Test
    @DisplayName("inscribir: debe inscribir exitosamente a un estudiante activo")
    void inscribir_estudianteActivo_inscribeCorrectamente() {
        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));
        when(estudianteClient.buscarPorId("est001")).thenReturn(estudianteActivo);
        when(repository.save(any(Curso.class))).thenReturn(cursoActivo);

        CursoResponseDTO resultado = service.inscribirEstudiante("curso001", inscripcionDTO);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).save(any(Curso.class));
        verify(estudianteClient, times(1)).buscarPorId("est001");
    }

    @Test
    @DisplayName("inscribir: debe lanzar CursoLlenoException cuando el curso está lleno")
    void inscribir_cursoLleno_lanzaExcepcion() {
        cursoActivo.setEstado(EstadoCurso.LLENO);
        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));

        assertThatThrownBy(() -> service.inscribirEstudiante("curso001", inscripcionDTO))
                .isInstanceOf(CursoLlenoException.class)
                .hasMessageContaining("curso001");

        verify(estudianteClient, never()).buscarPorId(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("inscribir: debe lanzar EstudianteInactivoException cuando el estudiante está inactivo")
    void inscribir_estudianteInactivo_lanzaExcepcion() {
        EstudianteClientDTO estudianteInactivo = new EstudianteClientDTO(
                "est001", "Juan", "Pérez", "juan@test.com", "INACTIVO"
        );

        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));
        when(estudianteClient.buscarPorId("est001")).thenReturn(estudianteInactivo);

        assertThatThrownBy(() -> service.inscribirEstudiante("curso001", inscripcionDTO))
                .isInstanceOf(EstudianteInactivoException.class)
                .hasMessageContaining("est001");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("inscribir: debe lanzar EstudianteYaInscritoException cuando el estudiante ya está en el curso")
    void inscribir_estudianteYaInscrito_lanzaExcepcion() {
        EstudianteInscrito yaInscrito = EstudianteInscrito.builder()
                .estudianteId("est001")
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .fechaInscripcion(LocalDateTime.now())
                .build();
        cursoActivo.getEstudiantesInscritos().add(yaInscrito);

        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));
        when(estudianteClient.buscarPorId("est001")).thenReturn(estudianteActivo);

        assertThatThrownBy(() -> service.inscribirEstudiante("curso001", inscripcionDTO))
                .isInstanceOf(EstudianteYaInscritoException.class);

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("inscribir: debe cambiar estado a LLENO cuando se alcanza la capacidad máxima")
    void inscribir_alcanzaCapacidadMaxima_cambiEstadoALleno() {
        cursoActivo.setCapacidadMaxima(1);
        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));
        when(estudianteClient.buscarPorId("est001")).thenReturn(estudianteActivo);
        when(repository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));

        service.inscribirEstudiante("curso001", inscripcionDTO);

        verify(repository).save(argThat(c ->
                c.getEstado() == EstadoCurso.LLENO
        ));
    }

    @Test
    @DisplayName("inscribir: debe lanzar FeignException si el servicio de estudiantes no responde")
    void inscribir_servicioEstudiantesCaido_propagaFeignException() {
        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));
        when(estudianteClient.buscarPorId("est001"))
                .thenThrow(FeignException.ServiceUnavailable.class);

        assertThatThrownBy(() -> service.inscribirEstudiante("curso001", inscripcionDTO))
                .isInstanceOf(FeignException.class);

        verify(repository, never()).save(any());
    }

    // ── desactivar ───────────────────────────────────────────

    @Test
    @DisplayName("desactivar: debe cambiar el estado a INACTIVO")
    void desactivar_cursoActivo_cambiaEstado() {
        when(repository.findById("curso001")).thenReturn(Optional.of(cursoActivo));
        when(repository.save(any())).thenReturn(cursoActivo);

        service.desactivar("curso001");

        verify(repository).save(argThat(c ->
                c.getEstado() == EstadoCurso.INACTIVO
        ));
    }

    @Test
    @DisplayName("desactivar: debe lanzar CursoNotFoundException si el id no existe")
    void desactivar_idInexistente_lanzaExcepcion() {
        when(repository.findById("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.desactivar("noexiste"))
                .isInstanceOf(CursoNotFoundException.class)
                .hasMessageContaining("noexiste");

        verify(repository, never()).save(any());
    }
}