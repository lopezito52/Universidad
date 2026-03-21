package com.universidad.servicio_estudiantes.dto;

import com.universidad.servicio_estudiantes.model.Estudiante.EstadoEstudiante;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteResponseDTO {

    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate fechaNacimiento;
    private EstadoEstudiante estado;
    private LocalDateTime creadoEn;
}