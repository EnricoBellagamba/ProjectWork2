package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.candidatura.NuovaCandidaturaRequest;
import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.CandidaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/candidature")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;
    private final UtenteRepository utenteRepository;
    private final CandidatoRepository candidatoRepository;

    public CandidaturaController(CandidaturaService candidaturaService, UtenteRepository utenteRepository, CandidatoRepository candidatoRepository) {
        this.candidaturaService = candidaturaService;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
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
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<Candidatura> createCandidatura(@RequestBody NuovaCandidaturaRequest request , Authentication authentication) {
        // 1. Dal token ricavo l'email dell'utente loggato
        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2. Trovo il CANDIDATO legato a questo utente
        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Profilo candidato non trovato"));

        // 3. Creo la candidatura usando idCandidato + idPosizione
        Candidatura nuova =  candidaturaService.createCandidatura(
                candidato.getIdCandidato(),
                request.getIdPosizione()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(nuova);
    }

    @GetMapping("/mie")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<Candidatura>> getMieCandidature(Authentication authentication) {

        String email = authentication.getName();

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato"));

        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Profilo candidato non trovato"));

        List<Candidatura> lista = candidaturaService.getCandidatureByCandidato(
                candidato.getIdCandidato()
        );

        return ResponseEntity.ok(lista);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidatura(@PathVariable Long id) {
        candidaturaService.deleteCandidatura(id);
        return ResponseEntity.noContent().build();
    }

}
