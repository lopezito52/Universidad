package com.universidad.servicio_cursos.repository;

import com.universidad.servicio_cursos.model.Curso;
import com.universidad.servicio_cursos.model.Curso.EstadoCurso;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends MongoRepository<Curso, String> {
    List<Curso> findByEstado(EstadoCurso estado);
    boolean existsByNombre(String nombre);
}