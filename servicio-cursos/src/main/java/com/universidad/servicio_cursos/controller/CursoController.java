package com.universidad.servicio_cursos.controller;

import com.universidad.servicio_cursos.dto.*;
import com.universidad.servicio_cursos.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService service;

    // POST /api/cursos
    @PostMapping
    public ResponseEntity<CursoResponseDTO> crear(
            @Valid @RequestBody CursoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    // GET /api/cursos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/cursos
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    // POST /api/cursos/{id}/inscribir
    @PostMapping("/{id}/inscribir")
    public ResponseEntity<CursoResponseDTO> inscribir(
            @PathVariable String id,
            @Valid @RequestBody InscripcionRequestDTO dto) {
        return ResponseEntity.ok(service.inscribirEstudiante(id, dto));
    }

    // PATCH /api/cursos/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}