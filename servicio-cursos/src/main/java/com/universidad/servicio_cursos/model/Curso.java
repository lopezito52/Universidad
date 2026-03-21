package com.universidad.servicio_cursos.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "cursos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    private String id;

    private String nombre;

    private String descripcion;

    private int capacidadMaxima;

    private EstadoCurso estado;

    @Builder.Default
    private List<EstudianteInscrito> estudiantesInscritos = new ArrayList<>();

    private LocalDateTime creadoEn;

    public enum EstadoCurso {
        ACTIVO, INACTIVO, LLENO
    }
}