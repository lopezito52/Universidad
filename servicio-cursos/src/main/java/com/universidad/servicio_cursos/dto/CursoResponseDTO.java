package com.universidad.servicio_cursos.dto;

import com.universidad.servicio_cursos.model.Curso.EstadoCurso;
import com.universidad.servicio_cursos.model.EstudianteInscrito;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoResponseDTO {
    private String id;
    private String nombre;
    private String descripcion;
    private int capacidadMaxima;
    private int inscritos;
    private EstadoCurso estado;
    private List<EstudianteInscrito> estudiantesInscritos;
    private LocalDateTime creadoEn;
}