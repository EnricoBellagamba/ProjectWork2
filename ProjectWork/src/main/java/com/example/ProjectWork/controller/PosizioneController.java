package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.StatoPosizione;
import com.example.ProjectWork.model.Settore;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/posizioni")
public class PosizioneController {

    private final PosizioneRepository posizioneRepository;
    private final UtenteRepository utenteRepository;
    private final PosizioneService posizioneService;

    public PosizioneController(PosizioneRepository posizioneRepository,
                               UtenteRepository utenteRepository,
                               PosizioneService posizioneService) {
        this.posizioneRepository = posizioneRepository;
        this.utenteRepository = utenteRepository;
        this.posizioneService = posizioneService;
    }

    // =========================================================
    //            ENDPOINT GENERICI POSIZIONI
    // =========================================================

    // GET /api/posizioni  -> usato dal frontend candidato
    @GetMapping
    public ResponseEntity<List<Posizione>> getAllPosizioni() {
        List<Posizione> posizioni = posizioneRepository.findAll();
        return ResponseEntity.ok(posizioni);
    }

    // GET /api/posizioni/{id} -> dettaglio posizione
    @GetMapping("/{id}")
    public ResponseEntity<Posizione> getPosizioneById(@PathVariable Long id) {
        Posizione posizione = posizioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata con id: " + id));
        return ResponseEntity.ok(posizione);
    }

    // =========================================================
    //           POSIZIONI CREATE DALL’HR LOGGATO
    // =========================================================

    /**
     * Endpoint chiamato dal frontend HR:
     * GET /api/posizioni/hr/mie
     *
     * Ritorna le posizioni create dall’HR corrente.
     */
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

    /**
     * Endpoint già usato dal form di creazione:
     * POST /api/posizioni
     *
     * Il body è direttamente l'entità Posizione serializzata da JSON.
     */
    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Posizione> createPosizione(
            @RequestBody Posizione posizione,
            Authentication authentication
    ) {
        // candidatureRicevute NOT NULL -> se nullo lo settiamo a 0
        if (posizione.getCandidatureRicevute() == null) {
            posizione.setCandidatureRicevute(0L);
        }

        // Se RAL è null, lo lasciamo null (la colonna lo consente), altrimenti va bene così

        // createdByHR NOT NULL -> usiamo l'utente HR loggato
        if (posizione.getCreatedByHR() == null) {
            if (authentication == null) {
                throw new RuntimeException("Utente HR non autenticato.");
            }

            String email = authentication.getName();

            Utente hr = utenteRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utente HR non trovato con email: " + email));

            posizione.setCreatedByHR(hr);
        }

        // pubblicataAt: la impostiamo alla data odierna se è null
        if (posizione.getPubblicataAt() == null) {
            posizione.setPubblicataAt(LocalDate.now());
        }

        // idStatoPosizione: se non arriva dal frontend, la mettiamo ad "aperta" (id = 1)
        if (posizione.getIdStatoPosizione() == null) {
            StatoPosizione statoAperta = new StatoPosizione();
            statoAperta.setIdStatoPosizione(1L); // ASSUNZIONE: 1 = APERTA
            posizione.setIdStatoPosizione(statoAperta);
        }

        // idSettore NOT NULL: se il frontend non lo manda, usiamo un settore di default (id = 1)
        if (posizione.getIdSettore() == null) {
            Settore settoreDefault = new Settore();
            settoreDefault.setIdSettore(1L); // ASSUNZIONE: esiste un settore con id=1
            posizione.setIdSettore(settoreDefault);
        }

        Posizione salvata = posizioneRepository.save(posizione);
        return ResponseEntity.ok(salvata);
    }

    // =========================================================
    //                     ELIMINAZIONE
    // =========================================================

    // Endpoint che il frontend usa già: DELETE /api/posizioni/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deletePosizione(@PathVariable Long id,
                                                Authentication authentication) {
        // eventualmente potresti verificare che la posizione appartenga all’HR loggato
        posizioneService.deletePosizione(id);
        return ResponseEntity.noContent().build();
    }
}
