package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.repository.CandidaturaRepository;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.StatoCandidaturaRepository;
import com.example.ProjectWork.service.CandidaturaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidaturaServiceImpl implements CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;

    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository) {
        this.candidaturaRepository = candidaturaRepository;
    }

    @Override
    public List<Candidatura> getAllCandidature() {
        return candidaturaRepository.findAll();
    }

    @Override
    public Candidatura createCandidatura(Candidatura candidatura) {
        return candidaturaRepository.save(candidatura);
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