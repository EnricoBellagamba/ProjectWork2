package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.CandidatoConPosizioneDTO;
import com.example.ProjectWork.model.Candidato;

import java.util.List;

/**
 * Interfaccia di servizio responsabile della gestione delle entità {@link Candidato}
 * e delle operazioni di creazione, aggiornamento, eliminazione e ricerca dei candidati,
 * incluse le interrogazioni basate sulla posizione lavorativa.
 */
public interface CandidatoService {

    /**
     * Restituisce l'elenco completo di tutti i candidati presenti nel sistema.
     *
     * @return una lista contenente tutte le istanze di {@link Candidato}
     */
    List<Candidato> getAllCandidati();

    /**
     * Crea e salva un nuovo candidato.
     *
     * @param candidato il candidato da creare
     * @return il candidato appena creato
     */
    Candidato createCandidato(Candidato candidato);

    /**
     * Elimina un candidato in base al suo identificativo.
     *
     * @param id l'ID del candidato da eliminare
     */
    void deleteCandidato(Long id);

    /**
     * Recupera un candidato tramite il suo identificativo.
     *
     * @param id l'ID del candidato da ricercare
     * @return l'istanza di {@link Candidato} corrispondente
     */
    Candidato getCandidatoById(Long id);

    /**
     * Aggiorna le informazioni di un candidato esistente.
     *
     * @param id l'ID del candidato da aggiornare
     * @param candidato l'oggetto contenente i dati aggiornati
     * @return il candidato aggiornato
     */
    Candidato updateCandidato(Long id, Candidato candidato);

    /**
     * Restituisce i candidati associati a una specifica posizione lavorativa,
     * filtrando in base al titolo della posizione.
     *
     * @param titolo il titolo della posizione su cui effettuare la ricerca
     * @return una lista di {@link CandidatoConPosizioneDTO} contenente
     *         i candidati e le relative informazioni di posizione
     */
    List<CandidatoConPosizioneDTO> getCandidatiByPosizione(String titolo);

}

