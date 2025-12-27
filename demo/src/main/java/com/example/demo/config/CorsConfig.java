package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer{
    // @Override
    // public void addCorsMappings(CorsRegistry registry) {
    
    //     registry.addMapping("/students/**")
    //         .allowedOrigins("http://localhost:8080")
    //         .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
    //         .allowedHeaders("*")
    //         .allowCredentials(true);

    
    //     registry.addMapping("/v3/api-docs/**")
    //         .allowedOrigins("http://localhost:8080")
    //         .allowedMethods("*")
    //         .allowedHeaders("*")
    //         .allowCredentials(false);

    //     registry.addMapping("/swagger-ui/**")
    //         .allowedOrigins("http://localhost:8080")
    //         .allowedMethods("*")
    //         .allowedHeaders("*")
    //         .allowCredentials(false);
    //     registry.addMapping("/api/auth/**") // ← Разрешаем CORS для всех эндпоинтов аутентификации
    //         .allowedOrigins("http://localhost:8080")
    //         .allowedMethods("*")
    //         .allowedHeaders("*")
    //         .allowCredentials(true);
    // }
}
