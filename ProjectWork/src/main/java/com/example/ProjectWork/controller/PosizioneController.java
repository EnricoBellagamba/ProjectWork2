package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posizioni")
public class PosizioneController {

    @Autowired
    private final PosizioneService posizioneService;

    public PosizioneController(PosizioneService posizioneService) {
        this.posizioneService = posizioneService;
    }

    @GetMapping
    public ResponseEntity<List<Posizione>> findAll() {
        List<Posizione> posizioni = posizioneService.getAllPosizioni();
        return ResponseEntity.ok(posizioni);
    }

    @PostMapping("/nuova")
    public ResponseEntity<Posizione> save(@RequestBody Posizione posizione) {
        //TODO:
        Posizione newPosizione = posizioneService.createPosizione(posizione);
        return ResponseEntity.ok(newPosizione);
    }
}
