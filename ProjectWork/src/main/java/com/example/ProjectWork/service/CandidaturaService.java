package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.StatoCandidatura;
import com.example.ProjectWork.model.Utente;

import java.util.List;

public interface CandidaturaService {

    List<Candidatura> getAllCandidature();

    Candidatura createCandidatura(Long idCandidato, Long idPosizione);

    /**
     * Restituisce le candidature dell'utente come entità (uso interno / HR).
     */
    List<Candidatura> getCandidatureByUtente(Utente utente);

    void deleteCandidatura(Long id);

    Candidatura getCandidaturaById(Long id);

    /**
     * Restituisce le candidature dell'utente loggato in forma DTO,
     * con punteggio e stato più leggibile.
     */
    List<CandidaturaMiaDto> getCandidatureDettaglioByUtente(Utente utente);

    /**
     * HR — Aggiorna lo stato della candidatura (ACCETTATA / RESPINTA).
     */
    Candidatura aggiornaStato(Long idCandidatura, StatoCandidatura nuovoStato);
}
