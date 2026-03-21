package com.universidad.servicio_estudiantes.service;

import com.universidad.servicio_estudiantes.dto.*;
import java.util.List;

public interface EstudianteService {
    EstudianteResponseDTO registrar(EstudianteRequestDTO dto);
    EstudianteResponseDTO buscarPorId(String id);
    EstudianteResponseDTO buscarPorEmail(String email);
    List<EstudianteResponseDTO> listarActivos();
    EstudianteResponseDTO actualizar(String id, EstudianteRequestDTO dto);
    void desactivar(String id);
}