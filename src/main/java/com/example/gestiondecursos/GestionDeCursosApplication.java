package com.example.gestiondecursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class GestionDeCursosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionDeCursosApplication.class, args);
    }

}