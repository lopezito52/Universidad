package com.universidad.servicio_estudiantes.repository;

import com.universidad.servicio_estudiantes.model.Estudiante;
import com.universidad.servicio_estudiantes.model.Estudiante.EstadoEstudiante;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends MongoRepository<Estudiante, String> {

    Optional<Estudiante> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Estudiante> findByEstado(EstadoEstudiante estado);
}