package com.universidad.servicio_cursos.exception;

public class EstudianteInactivoException extends RuntimeException {
    public EstudianteInactivoException(String estudianteId) {
        super("El estudiante con id: " + estudianteId + " está INACTIVO y no puede inscribirse");
    }
}