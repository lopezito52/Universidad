package com.universidad.servicio_estudiantes.exception;

public class EstudianteNotFoundException extends RuntimeException {
    public EstudianteNotFoundException(String id) {
        super("Estudiante no encontrado con id: " + id);
    }
}