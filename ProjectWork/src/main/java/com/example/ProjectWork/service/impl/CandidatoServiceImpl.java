package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.service.CandidatoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidatoServiceImpl implements CandidatoService {

    private final CandidatoRepository candidatoRepository;


    public CandidatoServiceImpl(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    @Override
    public List<Candidato> getAllCandidati() {
        return candidatoRepository.findAll();
    }

    @Override
    public Candidato createCandidato(Candidato candidato) {
        return candidatoRepository.save(candidato);
    }

    @Override
    public void deleteCandidato(Long id) {
        if (!candidatoRepository.existsById(id)) {
            throw new RuntimeException("Candidato non trovato con ID: " + id);
        }
        candidatoRepository.deleteById(id);
    }

    @Override
    public Candidato getCandidatoById(Long id) {
        return candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovato con ID: " + id));
    }

    @Override
    public Candidato updateCandidato(Long id, Candidato candidato) {

        Candidato candidatoEsistente = candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato non trovato con ID: " + id));

        candidatoEsistente.setActive(candidato.getActive());

        return candidatoRepository.save(candidatoEsistente);
    }
}
