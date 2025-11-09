package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Posizione;

import java.util.List;


public interface PosizioneService {
    List<Posizione> getAllPosizioni();
    Posizione createPosizione(Posizione posizione);
    void deletePosizione(Long id);
}
