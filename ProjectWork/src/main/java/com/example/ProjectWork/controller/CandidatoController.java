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

    @Autowired
    private CandidatoService candidatoService;
    @Autowired
    private CandidatoRepository candidatoRepository;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
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

}
