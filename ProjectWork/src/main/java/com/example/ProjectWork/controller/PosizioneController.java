package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.candidatura.CandidatoPerPosizioneDTO;
import com.example.ProjectWork.dto.candidatura.Top5Request;
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

    @GetMapping
    public ResponseEntity<List<Posizione>> getAllPosizioni() {
        return ResponseEntity.ok(posizioneRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Posizione> getPosizioneById(@PathVariable Long id) {
        Posizione posizione = posizioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata con id: " + id));
        return ResponseEntity.ok(posizione);
    }

    @GetMapping("/hr/mie")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<Posizione>> getPosizioniCreateDaMe(Authentication authentication) {

        if (authentication == null)
            throw new RuntimeException("Utente non autenticato.");

        String email = authentication.getName();

        Utente hr = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente HR non trovato con email: " + email));

        return ResponseEntity.ok(posizioneRepository.findByCreatedByHR(hr));
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Posizione> createPosizione(
            @RequestBody Posizione posizione,
            Authentication authentication
    ) {
        if (authentication == null)
            throw new RuntimeException("Utente HR non autenticato.");

        String email = authentication.getName();

        Utente hr = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente HR non trovato con email: " + email));

        posizione.setCreatedByHR(hr);


        if (posizione.getCandidatureRicevute() == null)
            posizione.setCandidatureRicevute(0L);

        if (posizione.getPubblicataAt() == null)
            posizione.setPubblicataAt(LocalDate.now());

        if (posizione.getIdStatoPosizione() == null) {
            StatoPosizione aperta = new StatoPosizione();
            aperta.setIdStatoPosizione(1L);
            posizione.setIdStatoPosizione(aperta);
        }

        if (posizione.getDescrizione() == null || posizione.getDescrizione().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il campo descrizione è obbligatorio."
            );
        }

        if (posizione.getIdSettore() == null || posizione.getIdSettore().getIdSettore() == null)
            throw new RuntimeException("Il campo idSettore è obbligatorio.");

        Settore settore = settoreRepository.findById(posizione.getIdSettore().getIdSettore())
                .orElseThrow(() -> new RuntimeException("Settore non trovato."));

        posizione.setIdSettore(settore);

        return ResponseEntity.ok(posizioneRepository.save(posizione));
    }

    // ===========================================================================================
    // CANDIDATI PER POSIZIONE
    // ===========================================================================================
    @GetMapping("/{idPosizione}/candidati")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<CandidatoPerPosizioneDTO>> getCandidatiPerPosizione(
            @PathVariable Long idPosizione) {

        return ResponseEntity.ok(posizioneService.getCandidatiPerPosizione(idPosizione));
    }

    // ===========================================================================================
    // SALVATAGGIO TOP 5
    // ===========================================================================================
    @PostMapping("/{idPosizione}/top5")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> salvaTop5(
            @PathVariable Long idPosizione,
            @RequestBody Top5Request req
    ) {
        posizioneService.salvaTop5(idPosizione, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deletePosizione(@PathVariable Long id,
                                                Authentication authentication) {
        posizioneService.deletePosizione(id);
        return ResponseEntity.noContent().build();
    }
}
