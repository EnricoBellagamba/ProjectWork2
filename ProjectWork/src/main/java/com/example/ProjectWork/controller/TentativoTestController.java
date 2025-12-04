package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.TentativoTest;
import com.example.ProjectWork.model.EsitoTentativo;
import com.example.ProjectWork.repository.EsitoTentativoRepository;
import com.example.ProjectWork.repository.TentativoTestRepository;
import com.example.ProjectWork.repository.CandidaturaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tentativi")
public class TentativoTestController {

    private final TentativoTestRepository tentativoTestRepository;
    private final EsitoTentativoRepository esitoTentativoRepository;
    private final CandidaturaRepository candidaturaRepository;

    public TentativoTestController(
            TentativoTestRepository tentativoTestRepository,
            EsitoTentativoRepository esitoTentativoRepository,
            CandidaturaRepository candidaturaRepository
    ) {
        this.tentativoTestRepository = tentativoTestRepository;
        this.esitoTentativoRepository = esitoTentativoRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    // ==============================================================
    // GET: tutti i tentativi di una candidatura
    // ==============================================================
    @GetMapping("/candidatura/{idCandidatura}")
    public ResponseEntity<List<TentativoTest>> getTentativiByCandidatura(
            @PathVariable Long idCandidatura
    ) {
        return ResponseEntity.ok(
                tentativoTestRepository.findAllByIdCandidatura(idCandidatura)
        );
    }

    // ==============================================================
    // PATCH: completa tentativo (salva punteggio + data completamento)
    // ==============================================================
    @PatchMapping("/{idTentativo}/completa")
    public ResponseEntity<?> completaTentativo(
            @PathVariable Long idTentativo,
            @RequestParam Integer punteggio
    ) {
        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        t.setPunteggioTotale(punteggio);
        t.setCompletatoAt(LocalDateTime.now());

        tentativoTestRepository.save(t);

        return ResponseEntity.ok().build();
    }

    // ==============================================================
    // PATCH: aggiorna esito tentativo
    // ==============================================================
    @PatchMapping("/{idTentativo}/esito")
    public ResponseEntity<?> aggiornaEsito(
            @PathVariable Long idTentativo,
            @RequestParam String esito
    ) {
        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        EsitoTentativo nuovo = esitoTentativoRepository.findByCodice(esito)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        t.setIdEsitoTentativo(nuovo);

        tentativoTestRepository.save(t);

        return ResponseEntity.ok().build();
    }
}
