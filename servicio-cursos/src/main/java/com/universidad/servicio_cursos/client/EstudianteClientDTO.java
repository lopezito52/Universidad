package com.universidad.servicio_cursos.client;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteClientDTO {
    private String id;
    private String nombre;
    private String apellido;
    private String email;
    private String estado; // "ACTIVO" o "INACTIVO"
}
