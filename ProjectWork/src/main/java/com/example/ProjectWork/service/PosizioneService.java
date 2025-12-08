package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.candidatura.CandidatoPerPosizioneDTO;
import com.example.ProjectWork.dto.candidatura.Top5Request;
import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;

import java.util.List;

/**
 * Interfaccia di servizio dedicata alla gestione delle entità {@link Posizione}.
 * Fornisce funzionalità per la creazione, eliminazione, consultazione e gestione
 * delle posizioni lavorative, incluse le operazioni specifiche per gli utenti HR
 * e le analisi sui candidati.
 */
public interface PosizioneService {

    /**
     * Restituisce tutte le posizioni lavorative presenti nel sistema.
     *
     * @return una lista di {@link Posizione}
     */
    List<Posizione> getAllPosizioni();

    /**
     * Crea una nuova posizione lavorativa.
     *
     * @param posizione l'oggetto {@link Posizione} da creare
     * @return la posizione appena creata
     */
    Posizione createPosizione(Posizione posizione);

    /**
     * Elimina una posizione lavorativa tramite il suo ID.
     *
     * @param id l'ID della posizione da eliminare
     */
    void deletePosizione(Long id);

    /**
     * Recupera una posizione tramite il suo identificativo.
     *
     * @param id l'ID della posizione da recuperare
     * @return l'istanza di {@link Posizione} corrispondente
     */
    Posizione getPosizioneById(Long id);

    /**
     * Restituisce tutte le posizioni create o gestite da un determinato HR.
     *
     * @param hr l'utente HR proprietario o referente delle posizioni
     * @return una lista di posizioni associate all'HR
     */
    List<Posizione> getPosizioniByHR(Utente hr);

    /**
     * Crea una posizione associandola a un determinato utente HR.
     *
     * @param posizione la posizione da creare
     * @param hr l’utente HR che la crea o la gestisce
     * @return la posizione appena creata e collegata all’HR
     */
    Posizione createPosizionePerHR(Posizione posizione, Utente hr);

    /**
     * Restituisce l’elenco dei candidati che hanno applicato a una specifica posizione,
     * includendo eventuali punteggi e dettagli utili.
     *
     * @param idPosizione l’ID della posizione per cui recuperare i candidati
     * @return una lista di {@link CandidatoPerPosizioneDTO}
     */
    List<CandidatoPerPosizioneDTO> getCandidatiPerPosizione(Long idPosizione);

    /**
     * Salva la "Top 5" (o comunque una selezione specificata) dei candidati
     * relativi a una posizione lavorativa in base ai dati forniti nella richiesta.
     *
     * @param idPosizione l’ID della posizione di riferimento
     * @param req l’oggetto {@link Top5Request} contenente gli ID dei candidati selezionati
     */
    void salvaTop5(Long idPosizione, Top5Request req);

    /**
     * Restituisce i migliori candidati per una posizione, ordinati secondo il criterio aziendale
     * (es. punteggio).
     *
     * @param idPosizione l’ID della posizione
     * @param limit il numero massimo di candidati da restituire
     * @return una lista di {@link CandidatoPerPosizioneDTO} contenente i top candidati
     */
    List<CandidatoPerPosizioneDTO> getTopCandidati(Long idPosizione, int limit);
}
