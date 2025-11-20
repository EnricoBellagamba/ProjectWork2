package com.example.ProjectWork.controller;

import com.example.ProjectWork.config.SecurityConfig;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // GET ALL
    @GetMapping
    public ResponseEntity<List<Utente>> getAllUtenti() {
        return ResponseEntity.ok(utenteService.getAllUtenti());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Utente> getUtenteById(@PathVariable Long id) {
        return ResponseEntity.ok(utenteService.getUtenteById(id));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Utente> createUtente(@Valid @RequestBody Utente utente) {
        return ResponseEntity.ok(utenteService.createUtente(utente));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Utente> updateUtente(
            @PathVariable Long id,
            @RequestBody Utente utenteDetails) {

        return ResponseEntity.ok(utenteService.updateUtente(id, utenteDetails));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id) {
            utenteService.deleteUtente(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idUtente}/preferiti/{idPosizione}")
    public ResponseEntity<?> aggiungiPosizionePreferita(
            @PathVariable Long idUtente,
            @PathVariable Long idPosizione) {

        utenteService.aggiungiPosizionePreferita(idUtente, idPosizione);
        return ResponseEntity.ok().build();
    }

}

