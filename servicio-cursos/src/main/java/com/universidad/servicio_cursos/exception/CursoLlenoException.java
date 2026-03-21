package com.universidad.servicio_cursos.exception;

public class CursoLlenoException extends RuntimeException {
    public CursoLlenoException(String cursoId) {
        super("El curso con id: " + cursoId + " ha alcanzado su capacidad máxima");
    }
}