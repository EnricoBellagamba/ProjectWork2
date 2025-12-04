package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Settore;
import com.example.ProjectWork.model.StatoPosizione;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.SettoreRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/posizioni")
public class PosizioneController {

    private final PosizioneRepository posizioneRepository;
    private final UtenteRepository utenteRepository;
    private final SettoreRepository settoreRepository;
    private final PosizioneService posizioneService;

    public PosizioneController(PosizioneRepository posizioneRepository,
                               UtenteRepository utenteRepository,
                               SettoreRepository settoreRepository,
                               PosizioneService posizioneService) {
        this.posizioneRepository = posizioneRepository;
        this.utenteRepository = utenteRepository;
        this.settoreRepository = settoreRepository;
        this.posizioneService = posizioneService;
    }

    // =========================================================
    //            ENDPOINT GENERICI POSIZIONI
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Posizione>> getAllPosizioni() {
        List<Posizione> posizioni = posizioneRepository.findAll();
        return ResponseEntity.ok(posizioni);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Posizione> getPosizioneById(@PathVariable Long id) {
        Posizione posizione = posizioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata con id: " + id));
        return ResponseEntity.ok(posizione);
    }

    // =========================================================
    //           POSIZIONI CREATE DALL’HR LOGGATO
    // =========================================================

    @GetMapping("/hr/mie")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<Posizione>> getPosizioniCreateDaMe(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Utente non autenticato.");
        }

        String email = authentication.getName();

        Utente hr = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente HR non trovato con email: " + email));

        List<Posizione> miePosizioni = posizioneRepository.findByCreatedByHR(hr);

        return ResponseEntity.ok(miePosizioni);
    }

    // =========================================================
    //              CREAZIONE NUOVA POSIZIONE HR
    // =========================================================

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Posizione> createPosizione(
            @RequestBody Posizione posizione,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new RuntimeException("Utente HR non autenticato.");
        }

        // HR creatore
        String email = authentication.getName();
        Utente hr = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente HR non trovato con email: " + email));
        posizione.setCreatedByHR(hr);


        // candidatureRicevute NOT NULL
        if (posizione.getCandidatureRicevute() == null) {
            posizione.setCandidatureRicevute(0L);
        }

        // pubblicataAt default oggi
        if (posizione.getPubblicataAt() == null) {
            posizione.setPubblicataAt(LocalDate.now());
        }

        // stato posizione default "aperta" (id=1) se non arriva niente
        if (posizione.getIdStatoPosizione() == null) {
            StatoPosizione statoAperta = new StatoPosizione();
            statoAperta.setIdStatoPosizione(1L);
            posizione.setIdStatoPosizione(statoAperta);
        }

        if (posizione.getDescrizione() == null || posizione.getDescrizione().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il campo descrizione è obbligatorio."
            );
        }

        // *** SETTORE OBBLIGATORIO ***
        if (posizione.getIdSettore() == null ||
                posizione.getIdSettore().getIdSettore() == null) {
            throw new RuntimeException("Il campo idSettore è obbligatorio.");
        }

        Long idSettoreRichiesto = posizione.getIdSettore().getIdSettore();

        Settore settore = settoreRepository.findById(idSettoreRichiesto)
                .orElseThrow(() ->
                        new RuntimeException("Settore non trovato con id: " + idSettoreRichiesto));

        posizione.setIdSettore(settore);

        Posizione salvata = posizioneRepository.save(posizione);
        return ResponseEntity.ok(salvata);
    }

    // =========================================================
    //                     ELIMINAZIONE
    // =========================================================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deletePosizione(@PathVariable Long id,
                                                Authentication authentication) {
        posizioneService.deletePosizione(id);
        return ResponseEntity.noContent().build();
    }
}
