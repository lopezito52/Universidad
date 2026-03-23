package com.universidad.servicio_estudiantes.service;

import com.universidad.servicio_estudiantes.dto.EstudianteRequestDTO;
import com.universidad.servicio_estudiantes.dto.EstudianteResponseDTO;
import com.universidad.servicio_estudiantes.exception.EmailDuplicadoException;
import com.universidad.servicio_estudiantes.exception.EstudianteNotFoundException;
import com.universidad.servicio_estudiantes.model.Estudiante;
import com.universidad.servicio_estudiantes.model.Estudiante.EstadoEstudiante;
import com.universidad.servicio_estudiantes.repository.EstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitarios - EstudianteService")
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository repository;

    @InjectMocks
    private EstudianteServiceImpl service;

    private Estudiante estudianteActivo;
    private EstudianteRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        estudianteActivo = Estudiante.builder()
                .id("abc123")
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .estado(EstadoEstudiante.ACTIVO)
                .creadoEn(LocalDateTime.now())
                .build();

        requestDTO = EstudianteRequestDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .build();
    }

    // ── registrar ────────────────────────────────────────────

    @Test
    @DisplayName("registrar: debe guardar y retornar el estudiante cuando el email no existe")
    void registrar_emailNuevo_retornaDTO() {
        when(repository.existsByEmail(anyString())).thenReturn(false);
        when(repository.save(any(Estudiante.class))).thenReturn(estudianteActivo);

        EstudianteResponseDTO resultado = service.registrar(requestDTO);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan@test.com");
        assertThat(resultado.getEstado()).isEqualTo(EstadoEstudiante.ACTIVO);
        verify(repository, times(1)).save(any(Estudiante.class));
    }

    @Test
    @DisplayName("registrar: debe lanzar EmailDuplicadoException cuando el email ya existe")
    void registrar_emailDuplicado_lanzaExcepcion() {
        when(repository.existsByEmail("juan@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(requestDTO))
                .isInstanceOf(EmailDuplicadoException.class)
                .hasMessageContaining("juan@test.com");

        verify(repository, never()).save(any());
    }

    // ── buscarPorId ──────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId: debe retornar el DTO cuando el id existe")
    void buscarPorId_idExistente_retornaDTO() {
        when(repository.findById("abc123")).thenReturn(Optional.of(estudianteActivo));

        EstudianteResponseDTO resultado = service.buscarPorId("abc123");

        assertThat(resultado.getId()).isEqualTo("abc123");
        assertThat(resultado.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("buscarPorId: debe lanzar EstudianteNotFoundException cuando el id no existe")
    void buscarPorId_idInexistente_lanzaExcepcion() {
        when(repository.findById("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("noexiste"))
                .isInstanceOf(EstudianteNotFoundException.class)
                .hasMessageContaining("noexiste");
    }

    // ── listarActivos ────────────────────────────────────────

    @Test
    @DisplayName("listarActivos: debe retornar solo los estudiantes con estado ACTIVO")
    void listarActivos_retornaSoloActivos() {
        when(repository.findByEstado(EstadoEstudiante.ACTIVO))
                .thenReturn(List.of(estudianteActivo));

        List<EstudianteResponseDTO> resultado = service.listarActivos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEstado()).isEqualTo(EstadoEstudiante.ACTIVO);
    }

    @Test
    @DisplayName("listarActivos: debe retornar lista vacía cuando no hay activos")
    void listarActivos_sinActivos_retornaListaVacia() {
        when(repository.findByEstado(EstadoEstudiante.ACTIVO)).thenReturn(List.of());

        List<EstudianteResponseDTO> resultado = service.listarActivos();

        assertThat(resultado).isEmpty();
    }

    // ── actualizar ───────────────────────────────────────────

    @Test
    @DisplayName("actualizar: debe actualizar los datos cuando el email nuevo no está en uso")
    void actualizar_emailDisponible_actualizaCorrectamente() {
        EstudianteRequestDTO nuevoDTO = EstudianteRequestDTO.builder()
                .nombre("Juan Carlos")
                .apellido("Pérez")
                .email("juancarlos@test.com")
                .fechaNacimiento(LocalDate.of(2000, 5, 15))
                .build();

        when(repository.findById("abc123")).thenReturn(Optional.of(estudianteActivo));
        when(repository.findByEmail("juancarlos@test.com")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(estudianteActivo);

        EstudianteResponseDTO resultado = service.actualizar("abc123", nuevoDTO);

        assertThat(resultado).isNotNull();
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("actualizar: debe lanzar EmailDuplicadoException si el email pertenece a otro estudiante")
    void actualizar_emailDeOtroEstudiante_lanzaExcepcion() {
        Estudiante otroEstudiante = Estudiante.builder()
                .id("otro456")
                .email("ocupado@test.com")
                .build();

        EstudianteRequestDTO dtoConEmailOcupado = EstudianteRequestDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("ocupado@test.com")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();

        when(repository.findById("abc123")).thenReturn(Optional.of(estudianteActivo));
        when(repository.findByEmail("ocupado@test.com")).thenReturn(Optional.of(otroEstudiante));

        assertThatThrownBy(() -> service.actualizar("abc123", dtoConEmailOcupado))
                .isInstanceOf(EmailDuplicadoException.class);

        verify(repository, never()).save(any());
    }

    // ── desactivar ───────────────────────────────────────────

    @Test
    @DisplayName("desactivar: debe cambiar el estado a INACTIVO")
    void desactivar_estudianteActivo_cambiaEstadoAInactivo() {
        when(repository.findById("abc123")).thenReturn(Optional.of(estudianteActivo));
        when(repository.save(any())).thenReturn(estudianteActivo);

        service.desactivar("abc123");

        verify(repository, times(1)).save(argThat(e ->
                e.getEstado() == EstadoEstudiante.INACTIVO
        ));
    }

    @Test
    @DisplayName("desactivar: debe lanzar EstudianteNotFoundException si el id no existe")
    void desactivar_idInexistente_lanzaExcepcion() {
        when(repository.findById("noexiste")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.desactivar("noexiste"))
                .isInstanceOf(EstudianteNotFoundException.class);

        verify(repository, never()).save(any());
    }
}