package com.universidad.servicio_cursos.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursoRequestDTO {

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @Min(value = 1, message = "La capacidad mínima es 1")
    @Max(value = 200, message = "La capacidad máxima es 200")
    private int capacidadMaxima;
}