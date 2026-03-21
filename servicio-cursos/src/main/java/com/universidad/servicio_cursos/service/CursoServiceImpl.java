package com.universidad.servicio_cursos.service;

import com.universidad.servicio_cursos.client.EstudianteClient;
import com.universidad.servicio_cursos.client.EstudianteClientDTO;
import com.universidad.servicio_cursos.dto.*;
import com.universidad.servicio_cursos.exception.*;
import com.universidad.servicio_cursos.model.Curso;
import com.universidad.servicio_cursos.model.Curso.EstadoCurso;
import com.universidad.servicio_cursos.model.EstudianteInscrito;
import com.universidad.servicio_cursos.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CursoService {

    private final CursoRepository repository;
    private final EstudianteClient estudianteClient;

    @Override
    public CursoResponseDTO crear(CursoRequestDTO dto) {
        Curso curso = Curso.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .capacidadMaxima(dto.getCapacidadMaxima())
                .estado(EstadoCurso.ACTIVO)
                .creadoEn(LocalDateTime.now())
                .build();
        return toDTO(repository.save(curso));
    }

    @Override
    public CursoResponseDTO buscarPorId(String id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    public List<CursoResponseDTO> listarActivos() {
        return repository.findByEstado(EstadoCurso.ACTIVO)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CursoResponseDTO inscribirEstudiante(String cursoId, InscripcionRequestDTO dto) {
        Curso curso = findOrThrow(cursoId);

        // Regla 1: El curso no puede estar lleno
        if (curso.getEstado() == EstadoCurso.LLENO) {
            throw new CursoLlenoException(cursoId);
        }

        // Regla 2: Consultar al servicio de estudiantes via REST
        EstudianteClientDTO estudiante = estudianteClient.buscarPorId(dto.getEstudianteId());

        // Regla 3: El estudiante debe estar ACTIVO
        if ("INACTIVO".equals(estudiante.getEstado())) {
            throw new EstudianteInactivoException(dto.getEstudianteId());
        }

        // Regla 4: El estudiante no puede estar ya inscrito
        boolean yaInscrito = curso.getEstudiantesInscritos().stream()
                .anyMatch(e -> e.getEstudianteId().equals(dto.getEstudianteId()));
        if (yaInscrito) {
            throw new EstudianteYaInscritoException(dto.getEstudianteId(), cursoId);
        }

        // Inscribir
        EstudianteInscrito inscrito = EstudianteInscrito.builder()
                .estudianteId(estudiante.getId())
                .nombre(estudiante.getNombre())
                .apellido(estudiante.getApellido())
                .email(estudiante.getEmail())
                .fechaInscripcion(LocalDateTime.now())
                .build();

        curso.getEstudiantesInscritos().add(inscrito);

        // Actualizar estado a LLENO si corresponde
        if (curso.getEstudiantesInscritos().size() >= curso.getCapacidadMaxima()) {
            curso.setEstado(EstadoCurso.LLENO);
        }

        return toDTO(repository.save(curso));
    }

    @Override
    public void desactivar(String id) {
        Curso curso = findOrThrow(id);
        curso.setEstado(EstadoCurso.INACTIVO);
        repository.save(curso);
    }

    // ── Helpers ──────────────────────────────────────────────
    private Curso findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new CursoNotFoundException(id));
    }

    private CursoResponseDTO toDTO(Curso c) {
        return CursoResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .capacidadMaxima(c.getCapacidadMaxima())
                .inscritos(c.getEstudiantesInscritos().size())
                .estado(c.getEstado())
                .estudiantesInscritos(c.getEstudiantesInscritos())
                .creadoEn(c.getCreadoEn())
                .build();
    }
}