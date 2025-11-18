package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.service.CandidatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidato")
public class CandidatoController {

    private final CandidatoService candidatoService;
    private final CandidatoRepository candidatoRepository;

    public CandidatoController(CandidatoService candidatoService, CandidatoRepository candidatoRepository) {
        this.candidatoService = candidatoService;
        this.candidatoRepository = candidatoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Candidato>> findAll() {
        return ResponseEntity.ok(candidatoService.getAllCandidati());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidato> getCandidatoById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(candidatoService.getCandidatoById(id));
    }

    @PostMapping
    public ResponseEntity<Candidato> createCandidato(@RequestBody Candidato candidato) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(candidatoService.createCandidato(candidato));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidato> updateCandidato(
            @PathVariable Long id,
            @RequestBody Candidato candidato) {

        return ResponseEntity.ok(candidatoService.updateCandidato(id, candidato));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidato(@PathVariable Long id) {
        candidatoService.deleteCandidato(id);
        return ResponseEntity.noContent().build();
    }

}
