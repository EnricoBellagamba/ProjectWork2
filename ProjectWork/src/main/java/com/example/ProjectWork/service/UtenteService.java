package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.utente.UpdatePasswordRequest;
import com.example.ProjectWork.dto.utente.UpdateProfiloCandidatoRequest;
import com.example.ProjectWork.dto.utente.UtenteDto;
import com.example.ProjectWork.model.Utente;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Interfaccia di servizio dedicata alla gestione degli utenti del sistema.
 * Fornisce funzionalità di creazione, aggiornamento del profilo e della password,
 * eliminazione e operazioni personalizzate come l'aggiunta delle posizioni preferite.
 */
public interface UtenteService {

    /**
     * Restituisce l’elenco completo di tutti gli utenti registrati nel sistema.
     *
     * @return una lista di {@link Utente}
     */
    List<Utente> getAllUtenti();

    /**
     * Recupera un utente tramite il suo identificativo.
     *
     * @param id l’ID dell’utente da recuperare
     * @return l’istanza di {@link Utente} corrispondente
     */
    Utente getUtenteById(Long id);

    /**
     * Crea e salva un nuovo utente nel sistema.
     *
     * @param utente l’oggetto {@link Utente} contenente i dati dell’utente da creare
     * @return l’utente appena creato
     */
    Utente createUtente(Utente utente);

    /**
     * Aggiorna i dati del profilo di un utente, incluso l’eventuale upload
     * di un nuovo curriculum vitae.
     *
     * @param id      l’ID dell’utente da aggiornare
     * @param req     i nuovi dati del profilo da applicare
     * @param cvFile  il file del CV aggiornato (opzionale)
     * @return un {@link UtenteDto} contenente il profilo aggiornato
     * @throws IOException se si verifica un errore durante la gestione del file CV
     */
    UtenteDto updateUtente(Long id, UpdateProfiloCandidatoRequest req, MultipartFile cvFile) throws IOException;

    /**
     * Aggiorna la password di un utente.
     *
     * @param id       l’ID dell’utente
     * @param request  l’oggetto contenente i dati necessari al cambio password
     */
    void updatePassword(Long id, UpdatePasswordRequest request);

    /**
     * Elimina un utente tramite il suo identificativo.
     *
     * @param id l’ID dell’utente da eliminare
     */
    void deleteUtente(Long id);

    /**
     * Aggiunge una posizione lavorativa alla lista delle preferenze dell’utente.
     *
     * @param idUtente    l’ID dell’utente
     * @param idPosizione l’ID della posizione da aggiungere alle preferenze
     */
    void aggiungiPosizionePreferita(Long idUtente, Long idPosizione);
}
