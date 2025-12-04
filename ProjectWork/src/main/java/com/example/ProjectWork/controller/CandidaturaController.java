package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.dto.candidatura.NuovaCandidaturaRequest;
import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.StatoCandidatura;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.repository.CandidaturaRepository;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.StatoCandidaturaRepository;
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
    private final StatoCandidaturaRepository statoCandidaturaRepository;

    public CandidaturaController(
            CandidaturaService candidaturaService,
            UtenteRepository utenteRepository,
            CandidatoRepository candidatoRepository,
            PosizioneRepository posizioneRepository,
            CandidaturaRepository candidaturaRepository,
            StatoCandidaturaRepository statoCandidaturaRepository
    ) {
        this.candidaturaService = candidaturaService;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
        this.posizioneRepository = posizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
    }

    // =========================================================
    // GET ALL
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Candidatura>> getAllCandidature() {
        return ResponseEntity.ok(candidaturaService.getAllCandidature());
    }

    // =========================================================
    // GET BY ID
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<Candidatura> getCandidaturaById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(candidaturaService.getCandidaturaById(id));
    }

    // =========================================================
    // CREATE CHECK — NO INSERT (usato per iniziare test)
    // =========================================================
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<Map<String, String>> createCandidatura(
            @RequestBody NuovaCandidaturaRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Utente non trovato"));

        Posizione posizione = posizioneRepository.findById(request.getIdPosizione())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Posizione non trovata"));

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

        Map<String, String> response = new HashMap<>();
        response.put("message", "Puoi procedere con il test");
        response.put("idPosizione", request.getIdPosizione().toString());
        response.put("idTest", posizione.getIdTest() != null ? posizione.getIdTest().toString() : null);

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // LE MIE CANDIDATURE
    // =========================================================
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

    // =========================================================
    // DELETE
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidatura(@PathVariable Long id) {
        candidaturaService.deleteCandidatura(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // HR — AGGIORNA STATO (ACCETTA / RIFIUTA)
    // =========================================================
    @PatchMapping("/{idCandidatura}/stato")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<String> aggiornaStatoCandidatura(
            @PathVariable Long idCandidatura,
            @RequestParam String stato
    ) {
        Candidatura candidatura = candidaturaRepository.findById(idCandidatura)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Candidatura non trovata"));

        StatoCandidatura statoNuovo = statoCandidaturaRepository.findByCodice(stato)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stato non valido"));

        candidatura.setStato(statoNuovo);
        candidaturaRepository.save(candidatura);

        return ResponseEntity.ok("OK");
    }
}
