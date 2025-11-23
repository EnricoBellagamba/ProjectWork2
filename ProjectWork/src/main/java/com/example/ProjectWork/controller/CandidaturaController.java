package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.service.CandidaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidature")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    public CandidaturaController(CandidaturaService candidaturaService) {
        this.candidaturaService = candidaturaService;
    }

    @GetMapping
    public ResponseEntity<List<Candidatura>> getAllCandidature() {
        return ResponseEntity.ok(candidaturaService.getAllCandidature());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidatura> getCandidaturaById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND).body(candidaturaService.getCandidaturaById(id));
    }

    @PostMapping
    public ResponseEntity<Candidatura> createCandidatura(@RequestBody Candidatura candidatura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(candidaturaService.createCandidatura(candidatura));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidatura(@PathVariable Long id) {
        candidaturaService.deleteCandidatura(id);
        return ResponseEntity.noContent().build();
    }

}
