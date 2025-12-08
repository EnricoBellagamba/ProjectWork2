package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.StatoCandidatura;
import com.example.ProjectWork.model.Utente;

import java.util.List;

/**
 * Interfaccia di servizio dedicata alla gestione delle entità {@link Candidatura},
 * fornendo operazioni di creazione, recupero, eliminazione e aggiornamento dello stato
 * delle candidature, sia lato HR che lato utente.
 */
public interface CandidaturaService {

    /**
     * Restituisce tutte le candidature presenti nel sistema.
     *
     * @return una lista completa di {@link Candidatura}
     */
    List<Candidatura> getAllCandidature();

    /**
     * Crea una nuova candidatura, collegando un candidato a una specifica posizione lavorativa.
     *
     * @param idCandidato l'ID del candidato che effettua la candidatura
     * @param idPosizione l'ID della posizione lavorativa alla quale ci si candida
     * @return la candidatura appena creata
     */
    Candidatura createCandidatura(Long idCandidato, Long idPosizione);

    /**
     * Restituisce tutte le candidature associate a un determinato utente
     * in formato entità. Utilizzato principalmente in ambito HR.
     *
     * @param utente l'utente di cui recuperare le candidature
     * @return una lista di {@link Candidatura}
     */
    List<Candidatura> getCandidatureByUtente(Utente utente);

    /**
     * Elimina una candidatura tramite il suo ID.
     *
     * @param id l'ID della candidatura da eliminare
     */
    void deleteCandidatura(Long id);

    /**
     * Recupera una candidatura tramite il suo identificativo.
     *
     * @param id l'ID della candidatura da recuperare
     * @return l'istanza di {@link Candidatura} corrispondente
     */
    Candidatura getCandidaturaById(Long id);

    /**
     * Restituisce le candidature dell'utente loggato in formato DTO,
     * includendo informazioni più leggibili quali punteggio e stato corrente.
     *
     * @param utente l'utente per cui estrarre il dettaglio candidature
     * @return una lista di {@link CandidaturaMiaDto} contenente le candidature arricchite
     */
    List<CandidaturaMiaDto> getCandidatureDettaglioByUtente(Utente utente);

    /**
     * Aggiorna lo stato di una candidatura (es. ACCETTATA o RESPINTA).
     * Funzionalità destinata al personale HR.
     *
     * @param idCandidatura l'ID della candidatura da aggiornare
     * @param nuovoStato    il nuovo stato da assegnare
     * @return la candidatura aggiornata
     */
    Candidatura aggiornaStato(Long idCandidatura, StatoCandidatura nuovoStato);
}
