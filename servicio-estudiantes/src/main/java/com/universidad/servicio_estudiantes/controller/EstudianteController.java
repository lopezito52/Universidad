package com.universidad.servicio_estudiantes.controller;

import com.universidad.servicio_estudiantes.dto.*;
import com.universidad.servicio_estudiantes.service.EstudianteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService service;

    // POST /api/estudiantes
    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> registrar(
            @Valid @RequestBody EstudianteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(dto));
    }

    // GET /api/estudiantes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET /api/estudiantes/email/{email}
    @GetMapping("/email/{email}")
    public ResponseEntity<EstudianteResponseDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(service.buscarPorEmail(email));
    }

    // GET /api/estudiantes
    @GetMapping
    public ResponseEntity<List<EstudianteResponseDTO>> listarActivos() {
        return ResponseEntity.ok(service.listarActivos());
    }

    // PUT /api/estudiantes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> actualizar(
            @PathVariable String id,
            @Valid @RequestBody EstudianteRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    // PATCH /api/estudiantes/{id}/desactivar
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable String id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}