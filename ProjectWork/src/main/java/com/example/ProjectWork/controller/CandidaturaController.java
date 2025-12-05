package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.dto.candidatura.CandidatoPerPosizioneDTO;
import com.example.ProjectWork.dto.candidatura.NuovaCandidaturaRequest;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.service.CandidaturaService;
import com.example.ProjectWork.service.PosizioneService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/candidature")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;
    private final PosizioneService posizioneService;

    private final UtenteRepository utenteRepository;
    private final CandidatoRepository candidatoRepository;
    private final PosizioneRepository posizioneRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;

    public CandidaturaController(
            CandidaturaService candidaturaService,
            PosizioneService posizioneService,
            UtenteRepository utenteRepository,
            CandidatoRepository candidatoRepository,
            PosizioneRepository posizioneRepository,
            CandidaturaRepository candidaturaRepository,
            StatoCandidaturaRepository statoCandidaturaRepository
    ) {
        this.candidaturaService = candidaturaService;
        this.posizioneService = posizioneService;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
        this.posizioneRepository = posizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
    }

    // ======================================================================================
    // GET tutte le candidature (uso interno / HR)
    // ======================================================================================
    @GetMapping
    public ResponseEntity<List<Candidatura>> getAllCandidature() {
        return ResponseEntity.ok(candidaturaService.getAllCandidature());
    }

    // ======================================================================================
    // GET candidatura singola
    // ======================================================================================
    @GetMapping("/{id}")
    public ResponseEntity<Candidatura> getCandidaturaById(@PathVariable Long id) {
        return ResponseEntity.ok(candidaturaService.getCandidaturaById(id));
    }

    // ======================================================================================
    // POST — creazione "logica" candidatura (usata per abilitare il test)
    // NON crea una candidatura vera nel DB: la crea solo alla consegna test
    // ======================================================================================
    @PostMapping
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<Map<String, String>> createCandidaturaRequest(
            @RequestBody NuovaCandidaturaRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // Recupera posizione
        Posizione posizione = posizioneRepository.findById(request.getIdPosizione())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Posizione non trovata"));

        // Recupero candidato
        Candidato candidato = candidatoRepository.findByIdUtente(utente).orElse(null);

        if (candidato != null) {
            boolean exists = candidaturaRepository
                    .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(
                            candidato.getIdCandidato(),
                            posizione.getIdPosizione()
                    );

            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Hai già una candidatura per questa posizione"
                );
            }
        }

        // Risposta per FE: non crea candidatura, ma permette di iniziare test
        Map<String, String> resp = new HashMap<>();
        resp.put("message", "Puoi procedere con il test");
        resp.put("idPosizione", posizione.getIdPosizione().toString());
        resp.put("idTest", posizione.getIdTest() != null ? posizione.getIdTest().toString() : null);

        return ResponseEntity.ok(resp);
    }

    // ======================================================================================
    // GET candidature dell’utente loggato
    // ======================================================================================
    @GetMapping("/mie")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<CandidaturaMiaDto>> getMieCandidature(Authentication authentication) {

        Utente utente = utenteRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(
                candidaturaService.getCandidatureDettaglioByUtente(utente)
        );
    }

    // ======================================================================================
    // DELETE candidatura
    // ======================================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidatura(@PathVariable Long id) {
        candidaturaService.deleteCandidatura(id);
        return ResponseEntity.noContent().build();
    }

    // ======================================================================================
    // HR — lista candidati per posizione (detta "schermata HR posizione")
    // ======================================================================================
    @GetMapping("/posizione/{idPosizione}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<CandidatoPerPosizioneDTO>> getCandidatiPerPosizione(
            @PathVariable Long idPosizione
    ) {
        return ResponseEntity.ok(
                posizioneService.getCandidatiPerPosizione(idPosizione)
        );
    }

    // ======================================================================================
    // HR — aggiorna stato candidatura (ACCETTATA / RESPINTA)
    // ======================================================================================
    @PatchMapping("/{idCandidatura}/stato")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, String>> aggiornaStato(
            @PathVariable Long idCandidatura,
            @RequestParam String stato
    ) {
        List<String> validi = List.of("ACCETTATA", "RESPINTA");

        if (!validi.contains(stato)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stato non valido");
        }

        StatoCandidatura nuovo = statoCandidaturaRepository.findByCodice(stato)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stato non trovato"));

        Candidatura aggiornata = candidaturaService.aggiornaStato(idCandidatura, nuovo);

        return ResponseEntity.ok(
                Map.of(
                        "status", "OK",
                        "idCandidatura", aggiornata.getIdCandidatura().toString(),
                        "nuovoStato", aggiornata.getStato().getCodice()
                )
        );
    }
}
