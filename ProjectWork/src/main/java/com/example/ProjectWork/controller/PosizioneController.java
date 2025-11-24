package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posizioni")
public class PosizioneController {

    private final PosizioneService posizioneService;
    private final PosizioneRepository posizioneRepository;
    private final UtenteRepository utenteRepository;

    public PosizioneController(PosizioneService posizioneService, PosizioneRepository posizioneRepository, UtenteRepository utenteRepository) {
        this.posizioneService = posizioneService;
        this.posizioneRepository = posizioneRepository;
        this.utenteRepository = utenteRepository;
    }

    // ========= ENDPOINT USATI DAI CANDIDATI =========

    // lista tutte le posizioni (aperte) – lato candidato
    @GetMapping
    public ResponseEntity<List<Posizione>> findAll() {
        return ResponseEntity.ok(posizioneService.getAllPosizioni());
    }

    @GetMapping("/topquattro")
    public ResponseEntity<List<Posizione>> topQuattro() {
        return ResponseEntity.ok(posizioneRepository.topQuattroPosizioni());
    }


    // lato candidato: dettaglio posizione
    @GetMapping("/{id}")
    public ResponseEntity<Posizione> getPosizioneById(@PathVariable Long id) {
        return ResponseEntity.ok(posizioneService.getPosizioneById(id));
    }

    // ==================  SEZIONE HR  ==================
    //tutte le posizioni create dall'HR loggato
    @GetMapping("/hr/mie")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<Posizione>> getPosizioniByHR(Authentication authentication) {
        return ResponseEntity.ok(posizioneService.getAllPosizioni());
    }

    //  è l'endpoint che il frontend usa già: POST /api/posizioni/nuova
    @PostMapping("/nuova")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Posizione> createPosizioneHR(@RequestBody Posizione posizione,
                                                       Authentication authentication) {

        String email = authentication.getName();
        Utente hr = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("HR non trovato"));

        // settiamo l'HR creatore e la data di pubblicazione
        posizione.setCreatedByHR(hr);
        if (posizione.getPubblicataAt() == null) {
            posizione.setPubblicataAt(java.time.LocalDate.now());
        }

        Posizione salvata = posizioneService.createPosizione(posizione);
        return ResponseEntity.ok(salvata);
    }

    // è l'endpoint che il frontend usa già: DELETE /api/posizioni/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deletePosizione(@PathVariable Long id,
                                                Authentication authentication) {
        // opzionale: potresti verificare che la posizione appartenga all'HR loggato
        posizioneService.deletePosizione(id);
        return ResponseEntity.noContent().build();
    }

}
