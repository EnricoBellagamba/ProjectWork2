package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.dto.candidatura.NuovaCandidaturaRequest;
import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.repository.CandidaturaRepository;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.CandidaturaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidature")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;
    private final UtenteRepository utenteRepository;
    private final CandidatoRepository candidatoRepository;
    private final PosizioneRepository posizioneRepository;
    private final CandidaturaRepository candidaturaRepository;

    public CandidaturaController(CandidaturaService candidaturaService,
                                 UtenteRepository utenteRepository,
                                 CandidatoRepository candidatoRepository, PosizioneRepository posizioneRepository, CandidaturaRepository candidaturaRepository) {
        this.candidaturaService = candidaturaService;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
        this.posizioneRepository = posizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    @GetMapping
    public ResponseEntity<List<Candidatura>> getAllCandidature() {
        return ResponseEntity.ok(candidaturaService.getAllCandidature());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidatura> getCandidaturaById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(candidaturaService.getCandidaturaById(id));
    }

    // CandidaturaController.java

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<Map<String, String>> createCandidatura(
            @RequestBody NuovaCandidaturaRequest request,
            Authentication authentication
    ) {
        // ✅ NON creare nulla, solo verifica che l'utente esista
        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato"));

        // ✅ Verifica che la posizione esista
        Posizione posizione = posizioneRepository.findById(request.getIdPosizione())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Posizione non trovata"));

        // ✅ Verifica che non ci sia già una candidatura
        Candidato candidato = candidatoRepository.findByIdUtente(utente).orElse(null);
        if (candidato != null) {
            boolean exists = candidaturaRepository
                    .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(
                            candidato.getIdCandidato(),
                            request.getIdPosizione()
                    );
            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Hai già una candidatura per questa posizione"
                );
            }
        }

        // ✅ Restituisci solo un messaggio di successo - NON creare nulla
        Map<String, String> response = new HashMap<>();
        response.put("message", "Puoi procedere con il test");
        response.put("idPosizione", request.getIdPosizione().toString());
        response.put("idTest", posizione.getIdTest() != null ? posizione.getIdTest().toString() : null);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/mie")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<CandidaturaMiaDto>> getMieCandidature(Authentication authentication) {

        String email = authentication.getName();

        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato"));

        List<CandidaturaMiaDto> lista = candidaturaService.getCandidatureDettaglioByUtente(utente);

        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidatura(@PathVariable Long id) {
        candidaturaService.deleteCandidatura(id);
        return ResponseEntity.noContent().build();
    }
}
