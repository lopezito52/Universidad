package com.universidad.servicio_cursos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionRequestDTO {

    @NotBlank(message = "El ID del estudiante es obligatorio")
    private String estudianteId;
}