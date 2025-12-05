package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.candidatura.CandidatoPerPosizioneDTO;
import com.example.ProjectWork.dto.candidatura.Top5Request;
import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;

import java.util.List;

public interface PosizioneService {

    List<Posizione> getAllPosizioni();

    Posizione createPosizione(Posizione posizione);

    void deletePosizione(Long id);

    Posizione getPosizioneById(Long id);

    List<Posizione> getPosizioniByHR(Utente hr);

    Posizione createPosizionePerHR(Posizione posizione, Utente hr);

    List<CandidatoPerPosizioneDTO> getCandidatiPerPosizione(Long idPosizione);

    void salvaTop5(Long idPosizione, Top5Request req);

    List<CandidatoPerPosizioneDTO> getTopCandidati(Long idPosizione, int limit);
}

