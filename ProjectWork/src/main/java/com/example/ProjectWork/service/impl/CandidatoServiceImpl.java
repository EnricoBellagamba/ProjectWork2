package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.CandidatoConPosizioneDTO;
import com.example.ProjectWork.model.Candidato;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.CandidatoRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.CandidatoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidatoServiceImpl implements CandidatoService {

    private final CandidatoRepository candidatoRepository;
    private final UtenteRepository utenteRepository;

    public CandidatoServiceImpl(CandidatoRepository candidatoRepository, UtenteRepository utenteRepository) {
        this.candidatoRepository = candidatoRepository;
        this.utenteRepository = utenteRepository;
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

        Candidato candidatoEsistente = candidatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidato non trovato: " + id));
        candidatoEsistente.setActive(false);
        candidatoRepository.save(candidatoEsistente);

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

        Utente utenteEsistente = candidatoEsistente.getIdUtente();
        Utente utenteNuovo = candidato.getIdUtente();

        if (utenteNuovo != null) {
            if (utenteNuovo.getNome() != null)
                utenteEsistente.setNome(utenteNuovo.getNome());

            if (utenteNuovo.getCognome() != null)
                utenteEsistente.setCognome(utenteNuovo.getCognome());

            if (utenteNuovo.getEmail() != null)
                utenteEsistente.setEmail(utenteNuovo.getEmail());

            if (utenteNuovo.getTelefono() != null)
                utenteEsistente.setTelefono(utenteNuovo.getTelefono());

            if (utenteNuovo.getCitta() != null)
                utenteEsistente.setCitta(utenteNuovo.getCitta());
        }

        utenteRepository.save(utenteEsistente);
        candidatoRepository.save(candidatoEsistente);

        return candidatoEsistente;
    }

    @Override
    public List<CandidatoConPosizioneDTO> getCandidatiByPosizione(String titolo) {
        return candidatoRepository.findAllByPosizioneDto(titolo);
    }
}
