package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.CandidatoConPosizioneDTO;
import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.service.CandidatoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/candidati")
public class CandidatoController {

    private final CandidatoService candidatoService;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @GetMapping
    public ResponseEntity<List<Candidato>> findAll() {
        return ResponseEntity.ok(candidatoService.getAllCandidati());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidato> getCandidatoById(@PathVariable Long id) {
        Candidato candidato = candidatoService.getCandidatoById(id);
        return ResponseEntity.ok(candidato); // 200 OK, NON 302 FOUND
    }

    @PostMapping
    public ResponseEntity<Candidato> createCandidato(@RequestBody Candidato candidato) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(candidatoService.createCandidato(candidato));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidato> updateCandidato(
            @PathVariable Long id,
            @RequestBody Candidato candidato) {

        return ResponseEntity.ok(candidatoService.updateCandidato(id, candidato));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidato(@PathVariable Long id) {
        candidatoService.deleteCandidato(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posizione/{titolo}")
    public List<CandidatoConPosizioneDTO> findCandidatiByPosizione(@PathVariable String titolo) {
        return candidatoService.getCandidatiByPosizione(titolo);
    }

    //TODO:
    // findAll, serve per trovare tutti i candidati per una determinata posizione  <- FATTO ->
    // getCandidatoByID il singolo canidato per candidatura (magari per vederne il dettaglio e scaricarne in CV)
    // createCandidato deve avvenire insieme alla creazione della candidatura
    // l'update cambia solamente il suo stato (attivo o no) in base allo stato
    // deleteCandidato facoltativa, alla fine dipende lo possiamo fare cascade nel momento in cui una candidatura viene eliminata
    // PER la candidatura utilizziamo questa classe / un altra classe?
    // CREAZIONE, MODIFICA (bella peso per il conto dei vari candidati), DELETE
}
