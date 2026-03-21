package com.universidad.servicio_estudiantes.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estudiante {

    @Id
    private String id;

    private String nombre;

    private String apellido;

    @Indexed(unique = true)
    private String email;

    private LocalDate fechaNacimiento;

    private EstadoEstudiante estado;

    private LocalDateTime creadoEn;

    public enum EstadoEstudiante {
        ACTIVO, INACTIVO
    }
}