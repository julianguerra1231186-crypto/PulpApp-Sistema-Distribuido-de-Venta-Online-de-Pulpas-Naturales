package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.JobApplicationRequestDTO;
import com.pulpapp.ms_users.dto.JobApplicationResponseDTO;
import com.pulpapp.ms_users.entity.JobApplication;
import com.pulpapp.ms_users.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationService {

    private final JobApplicationRepository repository;

    @Value("${app.uploads-dir:uploads/cv}")
    private String uploadsDir;

    /**
     * Guarda la postulación y el CV (si se adjuntó) en disco.
     */
    public JobApplicationResponseDTO saveApplication(
            JobApplicationRequestDTO dto,
            MultipartFile cvFile) throws IOException {

        JobApplication entity = new JobApplication();
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setPosition(dto.getPosition());
        entity.setMessage(dto.getMessage());

        // Guardar archivo si viene adjunto
        if (cvFile != null && !cvFile.isEmpty()) {
            String savedName = saveFile(cvFile);
            entity.setCvFile(savedName);
        }

        JobApplication saved = repository.save(entity);
        return toResponse(saved);
    }

    /**
     * Lista todas las postulaciones ordenadas por fecha descendente.
     */
    public List<JobApplicationResponseDTO> findAll() {
        return repository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retorna el Path del archivo CV para descarga.
     */
    public Path getCvPath(Long id) {
        JobApplication app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada: " + id));

        if (app.getCvFile() == null || app.getCvFile().isBlank()) {
            throw new RuntimeException("Esta postulación no tiene CV adjunto");
        }

        Path filePath = Paths.get(uploadsDir).resolve(app.getCvFile()).normalize();

        if (!Files.exists(filePath)) {
            throw new RuntimeException("Archivo no encontrado en el servidor");
        }

        return filePath;
    }

    public String getCvFileName(Long id) {
        return repository.findById(id)
                .map(JobApplication::getCvFile)
                .orElseThrow(() -> new RuntimeException("Postulación no encontrada"));
    }

    // ── Helpers ──────────────────────────────────────────────

    private String saveFile(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadsDir);
        Files.createDirectories(dir);

        // Nombre único para evitar colisiones
        String ext      = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + ext;
        Path   target   = dir.resolve(fileName);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    private JobApplicationResponseDTO toResponse(JobApplication e) {
        JobApplicationResponseDTO dto = new JobApplicationResponseDTO();
        dto.setId(e.getId());
        dto.setFullName(e.getFullName());
        dto.setEmail(e.getEmail());
        dto.setPhone(e.getPhone());
        dto.setPosition(e.getPosition());
        dto.setMessage(e.getMessage());
        dto.setCvFile(e.getCvFile());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}
