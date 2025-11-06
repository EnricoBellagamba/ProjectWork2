package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.repository.PosizioneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



}
