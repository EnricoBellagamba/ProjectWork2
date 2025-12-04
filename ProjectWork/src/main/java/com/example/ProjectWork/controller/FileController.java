package com.example.ProjectWork.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/files")
public class FileController {

    // Path RELATIVA ripristinata
    private final String basePath = "uploads/cv/";

    @GetMapping("/cv/{filename}")
    public ResponseEntity<FileSystemResource> getCv(@PathVariable String filename) {

        filename = filename.replace("\\", "").replace("/", "");

        File file = new File(basePath + filename);

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.badRequest().body(null);
        }

        FileSystemResource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
