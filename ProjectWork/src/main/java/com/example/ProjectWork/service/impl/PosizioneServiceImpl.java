package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosizioneServiceImpl implements PosizioneService {

    private final PosizioneRepository posizioneRepository;

    public PosizioneServiceImpl(PosizioneRepository posizioneRepository) {
        this.posizioneRepository = posizioneRepository;
    }

    @Override
    public List<Posizione> getAllPosizioni() {
        return posizioneRepository.findAll();
    }

    //TODO: c'e' da testare con POSTMAN createPosizione
    @Override
    public Posizione createPosizione(Posizione posizione) {
        return posizioneRepository.save(posizione);
    }

    @Override
    public void deletePosizione(Long id) {
        if (!posizioneRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        posizioneRepository.deleteById(id);
    }

}
