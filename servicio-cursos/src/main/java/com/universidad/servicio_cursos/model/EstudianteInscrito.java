package com.universidad.servicio_cursos.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstudianteInscrito {
    private String estudianteId;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDateTime fechaInscripcion;
}