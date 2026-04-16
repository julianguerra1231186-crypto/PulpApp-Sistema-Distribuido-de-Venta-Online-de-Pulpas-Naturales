package com.pulpapp.msproducts.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class CorsConfig {

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Sirve las imágenes subidas en /images/**
                String absolutePath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
                registry.addResourceHandler("/images/**")
                        .addResourceLocations(absolutePath);
            }
        };
    }
}
