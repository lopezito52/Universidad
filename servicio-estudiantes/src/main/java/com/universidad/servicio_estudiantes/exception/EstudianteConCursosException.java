package com.universidad.servicio_estudiantes.exception;


public class EstudianteConCursosException extends RuntimeException {
    public EstudianteConCursosException(String id) {
        super("No se puede desactivar el estudiante con id: " + id + " porque tiene cursos activos");
    }
}