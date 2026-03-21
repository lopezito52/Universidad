package com.universidad.servicio_cursos.service;

import com.universidad.servicio_cursos.dto.*;
import java.util.List;

public interface CursoService {
    CursoResponseDTO crear(CursoRequestDTO dto);
    CursoResponseDTO buscarPorId(String id);
    List<CursoResponseDTO> listarActivos();
    CursoResponseDTO inscribirEstudiante(String cursoId, InscripcionRequestDTO dto);
    void desactivar(String id);
}