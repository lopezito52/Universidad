package com.universidad.servicio_cursos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "servicio-estudiantes", url = "${estudiantes.service.url}")
public interface EstudianteClient {

    @GetMapping("/api/estudiantes/{id}")
    EstudianteClientDTO buscarPorId(@PathVariable("id") String id);
}