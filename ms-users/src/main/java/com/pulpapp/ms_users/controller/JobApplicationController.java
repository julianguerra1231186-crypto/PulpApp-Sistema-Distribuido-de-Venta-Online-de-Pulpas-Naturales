package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.JobApplicationRequestDTO;
import com.pulpapp.ms_users.dto.JobApplicationResponseDTO;
import com.pulpapp.ms_users.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService service;

    /** POST /job-applications — público */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public JobApplicationResponseDTO create(
            @Valid @ModelAttribute JobApplicationRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile cvFile
    ) throws IOException {
        return service.saveApplication(dto, cvFile);
    }

    /** GET /job-applications — solo ADMIN */
    @GetMapping
    public List<JobApplicationResponseDTO> getAll() {
        return service.findAll();
    }

    /**
     * GET /job-applications/{id}/download — descarga el CV adjunto.
     * Solo ADMIN (configurado en SecurityConfig).
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadCv(@PathVariable Long id) throws IOException {
        Path filePath = service.getCvPath(id);
        Resource resource = new UrlResource(filePath.toUri());

        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String header = "attachment; filename=\"" + filePath.getFileName().toString() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, header)
                .body(resource);
    }
}
