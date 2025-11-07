package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.repository.PosizioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posizione")
public class PosizioneController {

    @Autowired
    private PosizioneRepository posizioneRepo;

    @GetMapping
    public ResponseEntity<List<Posizione>> findAll() {
        return ResponseEntity.ok(posizioneRepo.findAll());
    }

    @PostMapping("/nuova")
    public ResponseEntity<Posizione> save(@RequestBody Posizione posizione) {
        return ResponseEntity.ok(posizioneRepo.save(posizione));
    }



}
