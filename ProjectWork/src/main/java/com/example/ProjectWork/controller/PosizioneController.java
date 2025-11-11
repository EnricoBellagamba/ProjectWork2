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

    @Autowired
    private PosizioneRepository posizioneRepository;

    public PosizioneController(PosizioneService posizioneService) {
        this.posizioneService = posizioneService;
    }

    @GetMapping
    public ResponseEntity<List<Posizione>> findAll() {
        List<Posizione> posizioni = posizioneService.getAllPosizioni();
        return ResponseEntity.ok(posizioni);
    }

    @GetMapping("/topquattro")
    public ResponseEntity<List<Posizione>> topQuattro() {
        return ResponseEntity.ok(posizioneRepository.topQuattroPosizioni());
    }

    @PostMapping("/nuova")
    public ResponseEntity<Posizione> save(@RequestBody Posizione posizione) {
        Posizione newPosizione = posizioneService.createPosizione(posizione);
        return ResponseEntity.ok(newPosizione);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosizione(@PathVariable Long id){
        posizioneService.deletePosizione(id);
        return ResponseEntity.noContent().build();

    }
}
