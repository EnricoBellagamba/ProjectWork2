package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.repository.CandidaturaRepository;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.StatoCandidaturaRepository;
import com.example.ProjectWork.service.CandidaturaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CandidaturaServiceImpl implements CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final CandidatoRepository candidatoRepository;
    private final PosizioneRepository posizioneRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;

    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository, CandidatoRepository candidatoRepository, PosizioneRepository posizioneRepository, StatoCandidaturaRepository statoCandidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
        this.candidatoRepository = candidatoRepository;
        this.posizioneRepository = posizioneRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
    }

    @Override
    public List<Candidatura> getAllCandidature() {
        return candidaturaRepository.findAll();
    }

    @Override
    public Candidatura createCandidatura(Long idCandidato, Long idPosizione) {
        // Evita candidature duplicate sulla stessa posizione
        boolean exists = candidaturaRepository
                .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(idCandidato, idPosizione);
        if (exists) {
            throw new IllegalStateException("Esiste già una candidatura per questa posizione");
        }

        Candidato candidato = candidatoRepository.findById(idCandidato)
                .orElseThrow(() -> new IllegalArgumentException("Candidato non trovato"));

        Posizione posizione = posizioneRepository.findById(idPosizione)
                .orElseThrow(() -> new IllegalArgumentException("Posizione non trovata"));

        // Recupera lo stato iniziale dal DB (es. "INVIATA" o "NUOVA")
        StatoCandidatura statoIniziale = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                .orElseThrow(() -> new IllegalArgumentException("Stato candidatura 'IN_VALUTAZIONE' non trovato"));
        Candidatura candidatura = new Candidatura();
        candidatura.setCandidato(candidato);
        candidatura.setPosizione(posizione);
        candidatura.setStato(statoIniziale);
        candidatura.setCreatedAT(LocalDate.now());
        return candidaturaRepository.save(candidatura);
    }

    @Override
    public List<Candidatura> getCandidatureByUtente(Utente utente) {
        return candidaturaRepository.findByCandidato_IdUtente(utente);
    }

    @Override
    public void deleteCandidatura(Long id) {
        if (!candidaturaRepository.existsById(id)) {
            throw new RuntimeException("Candidatura non trovata con ID: " + id);
        }
        candidaturaRepository.deleteById(id);
    }

    @Override
    public Candidatura getCandidaturaById(Long id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovato con ID: " + id));
    }

}