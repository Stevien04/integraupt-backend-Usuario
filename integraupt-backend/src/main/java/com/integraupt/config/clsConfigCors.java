package com.integraupt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuración de CORS para permitir peticiones desde el frontend React
 * Permite conexiones desde http://localhost:5173 (Vite) y otros puertos comunes
 * 
 * @author IntegraUPT Team
 * @version 1.0.0
 */
@Configuration
public class clsConfigCors {

    /**
     * Configura el filtro CORS para toda la aplicación
     * @return CorsFilter configurado
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciales
        config.setAllowCredentials(true);
        
        // Orígenes permitidos (frontend React)
        config.addAllowedOrigin("http://localhost:5173"); // Vite
        config.addAllowedOrigin("http://localhost:3000"); // Create React App
        config.addAllowedOrigin("http://localhost:4173"); // Vite Preview
        
        // Permitir todos los headers
        config.addAllowedHeader("*");
        
        // Permitir todos los métodos HTTP
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");
        
        // Aplicar configuración a todas las rutas
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
