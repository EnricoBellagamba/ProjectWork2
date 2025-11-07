package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    private final UtenteService utenteService;

    public UtenteController(UtenteService utenteService) {
        this.utenteService = utenteService;
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Utente>> getAllUtenti() {
        List<Utente> utenti = utenteService.getAllUtenti();
        return ResponseEntity.ok(utenti);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Utente> getUtenteById(@PathVariable Long id) {
        Utente utente = utenteService.getUtenteById(id);
        return ResponseEntity.ok(utente);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Utente> createUtente(@RequestBody Utente utente) {
        Utente newUtente = utenteService.createUtente(utente);
        return ResponseEntity.ok(newUtente);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Utente> updateUtente(
            @PathVariable Long id,
            @RequestBody Utente utenteDetails) {

        Utente update = utenteService.updateUtente(id, utenteDetails);
        return ResponseEntity.ok(update);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id) {
        utenteService.deleteUtente(id);
        return ResponseEntity.noContent().build();
    }
}

