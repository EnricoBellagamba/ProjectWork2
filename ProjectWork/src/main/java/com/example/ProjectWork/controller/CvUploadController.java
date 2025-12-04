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

    private static final String BASE_PATH =
            "C:\\Users\\ferric\\IdeaProjects\\ProjectWork2\\uploads\\cv\\";

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

        dest.getParentFile().mkdirs(); // crea cartella se manca
        file.transferTo(dest);

        candidato.getIdUtente().setCvUrl(dest.getAbsolutePath());
        candidatoRepository.save(candidato);

        return ResponseEntity.ok("CV caricato");
    }
}
