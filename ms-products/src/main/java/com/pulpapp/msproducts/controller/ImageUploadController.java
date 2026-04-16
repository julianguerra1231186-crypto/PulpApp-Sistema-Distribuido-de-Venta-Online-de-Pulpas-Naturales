package com.pulpapp.msproducts.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoint para subir imágenes de productos.
 * Guarda el archivo en disco y devuelve la URL pública para accederlo.
 */
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ImageUploadController {

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8082}")
    private String baseUrl;

    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El archivo está vacío"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Solo se permiten imágenes"));
        }

        // Crea el directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);

        // Nombre único para evitar colisiones
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String imageUrl = baseUrl + "/images/" + fileName;
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }
}
