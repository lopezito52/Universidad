package com.universidad.servicio_estudiantes.service;

import com.universidad.servicio_estudiantes.dto.*;
import com.universidad.servicio_estudiantes.exception.*;
import com.universidad.servicio_estudiantes.model.Estudiante;
import com.universidad.servicio_estudiantes.model.Estudiante.EstadoEstudiante;
import com.universidad.servicio_estudiantes.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteServiceImpl implements EstudianteService {

    private final EstudianteRepository repository;

    @Override
    public EstudianteResponseDTO registrar(EstudianteRequestDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new EmailDuplicadoException(dto.getEmail());
        }

        Estudiante estudiante = Estudiante.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .fechaNacimiento(dto.getFechaNacimiento())
                .estado(EstadoEstudiante.ACTIVO)
                .creadoEn(LocalDateTime.now())
                .build();

        return toDTO(repository.save(estudiante));
    }

    @Override
    public EstudianteResponseDTO buscarPorId(String id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    public EstudianteResponseDTO buscarPorEmail(String email) {
        Estudiante estudiante = repository.findByEmail(email)
                .orElseThrow(() -> new EstudianteNotFoundException(email));
        return toDTO(estudiante);
    }

    @Override
    public List<EstudianteResponseDTO> listarActivos() {
        return repository.findByEstado(EstadoEstudiante.ACTIVO)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EstudianteResponseDTO actualizar(String id, EstudianteRequestDTO dto) {
        Estudiante estudiante = findOrThrow(id);

        // Verifica que el nuevo email no esté en uso por OTRO estudiante
        repository.findByEmail(dto.getEmail())
                .filter(e -> !e.getId().equals(id))
                .ifPresent(e -> { throw new EmailDuplicadoException(dto.getEmail()); });

        estudiante.setNombre(dto.getNombre());
        estudiante.setApellido(dto.getApellido());
        estudiante.setEmail(dto.getEmail());
        estudiante.setFechaNacimiento(dto.getFechaNacimiento());

        return toDTO(repository.save(estudiante));
    }

    @Override
    public void desactivar(String id) {
        Estudiante estudiante = findOrThrow(id);
        estudiante.setEstado(EstadoEstudiante.INACTIVO);
        repository.save(estudiante);
    }

    // ── Helpers ──────────────────────────────────────────────
    private Estudiante findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EstudianteNotFoundException(id));
    }

    private EstudianteResponseDTO toDTO(Estudiante e) {
        return EstudianteResponseDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .fechaNacimiento(e.getFechaNacimiento())
                .estado(e.getEstado())
                .creadoEn(e.getCreadoEn())
                .build();
    }
}