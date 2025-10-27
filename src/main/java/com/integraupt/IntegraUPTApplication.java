package com.integraupt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Clase principal de la aplicación IntegraUPT Backend
 * Sistema de gestión para la Universidad Privada de Tacna
 * 
 * @author IntegraUPT Team
 * @version 1.0.0
 */
@SpringBootApplication
public class IntegraUPTApplication extends SpringBootServletInitializer {

    /**
     * Método principal para ejecutar la aplicación Spring Boot
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(IntegraUPTApplication.class, args);
        
        System.out.println("\n");
        System.out.println("=================================================");
        System.out.println("   INTEGRAUPT BACKEND - INICIADO CORRECTAMENTE  ");
        System.out.println("=================================================");
        System.out.println("   Puerto: 8080");
        System.out.println("   API Base URL: http://localhost:8080/api");
        System.out.println("   Base de Datos: dbIntegraUPT (MySQL)");
        System.out.println("=================================================");
        System.out.println("\n");
    }

    /**
     * Configuración para despliegue en servidores externos (WAR)
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IntegraUPTApplication.class);
    }
}
