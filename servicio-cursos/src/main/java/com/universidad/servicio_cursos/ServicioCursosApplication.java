package com.universidad.servicio_cursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients  // ← Esta anotación es clave
public class ServicioCursosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServicioCursosApplication.class, args);
    }
}