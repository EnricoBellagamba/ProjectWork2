package com.example.ProjectWork.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String BASE_UPLOAD_DIR =
            "C:\\Users\\ferric\\IdeaProjects\\ProjectWork2\\uploads";

    @PostMapping("/cv")
    public ResponseEntity<String> uploadCv(@RequestParam("file") MultipartFile file) throws IOException {

        String folder = BASE_UPLOAD_DIR + File.separator + "cv";
        File dir = new File(folder);
        if (!dir.exists()) dir.mkdirs();

        String extension = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf('.'));

        String fileName = "cv-" + UUID.randomUUID() + extension;

        File destination = new File(dir, fileName);
        file.transferTo(destination);

        // RITORNA SOLO IL NOME DEL FILE
        return ResponseEntity.ok(fileName);
    }
}
