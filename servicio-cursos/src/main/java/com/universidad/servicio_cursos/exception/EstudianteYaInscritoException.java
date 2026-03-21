package com.universidad.servicio_cursos.exception;

public class EstudianteYaInscritoException extends RuntimeException {
    public EstudianteYaInscritoException(String estudianteId, String cursoId) {
        super("El estudiante " + estudianteId + " ya está inscrito en el curso " + cursoId);
    }
}
