package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posizioni")
public class PosizioneController {

    private final PosizioneService posizioneService;

    private final PosizioneRepository posizioneRepository;

    public PosizioneController(PosizioneService posizioneService, PosizioneRepository posizioneRepository) {
        this.posizioneService = posizioneService;
        this.posizioneRepository = posizioneRepository;
    }

    @GetMapping
    public ResponseEntity<List<Posizione>> findAll() {
        return ResponseEntity.ok(posizioneService.getAllPosizioni());
    }

    @GetMapping("/topquattro")
    public ResponseEntity<List<Posizione>> topQuattro() {
        return ResponseEntity.ok(posizioneRepository.topQuattroPosizioni());
    }

    @PostMapping("/nuova")
    public ResponseEntity<Posizione> save(@RequestBody Posizione posizione) {
        return ResponseEntity.ok(posizioneService.createPosizione(posizione));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosizione(@PathVariable Long id){
        posizioneService.deletePosizione(id);
        return ResponseEntity.noContent().build();

    }

    // GET POSIZIONE BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Posizione> getPosizioneById(@PathVariable Long id) {
        return ResponseEntity.ok(posizioneService.getPosizioneById(id));
    }

}
