package com.universidad.servicio_cursos.exception;

public class CursoNotFoundException extends RuntimeException {
    public CursoNotFoundException(String id) {
        super("Curso no encontrado con id: " + id);
    }
}