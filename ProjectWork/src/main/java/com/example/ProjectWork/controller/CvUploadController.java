package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.repository.CandidatoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/cv")
public class CvUploadController {

    private final CandidatoRepository candidatoRepository;

    // Path RELATIVA ripristinata
    private static final String BASE_PATH = "uploads/cv/";

    public CvUploadController(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    @PostMapping("/upload/{idCandidato}")
    public ResponseEntity<?> uploadCv(
            @PathVariable Long idCandidato,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        Candidato candidato = candidatoRepository.findById(idCandidato)
                .orElseThrow(() -> new RuntimeException("Candidato non trovato"));

        String filename = "cv_" + idCandidato + ".pdf";

        File dest = new File(BASE_PATH + filename);

        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        // Salvo solo path relativa
        candidato.getIdUtente().setCvUrl(filename);
        candidatoRepository.save(candidato);

        return ResponseEntity.ok("CV caricato");
    }
}
