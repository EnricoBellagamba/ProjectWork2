package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Test;

import java.util.List;

/**
 * Interfaccia di servizio dedicata alla gestione dei test di valutazione.
 * Fornisce operazioni CRUD e applica le regole di business relative
 * alla durata, alla struttura e alla validazione dei punteggi.
 */
public interface TestService {

    /**
     * Restituisce la lista completa di tutti i test presenti nel sistema.
     *
     * @return una lista di {@link Test}
     */
    List<Test> getAllTests();

    /**
     * Recupera un test tramite il suo identificativo.
     *
     * @param id l'ID del test da recuperare
     * @return l'entità {@link Test} corrispondente
     * @throws RuntimeException se nessun test corrispondente viene trovato
     */
    Test getTestById(Long id);

    /**
     * Crea e salva un nuovo test.
     * Durante la creazione vengono applicati i vincoli di business
     * relativi a durata massima, numero minimo/massimo di domande
     * e coerenza dei punteggi.
     *
     * @param newTest l'entità {@link Test} da creare
     * @return il test creato
     */
    Test createTest(Test newTest);

    /**
     * Aggiorna un test esistente applicando le regole di validazione
     * su durata, domande e punteggi.
     *
     * @param id   l'ID del test da aggiornare
     * @param test l'entità contenente i dati aggiornati
     * @return il test aggiornato
     * @throws RuntimeException se il test da aggiornare non esiste
     */
    Test updateTest(Long id, Test test);

    /**
     * Elimina un test tramite il suo identificativo.
     *
     * @param idTest l'ID del test da eliminare
     */
    void deleteTest(Long idTest);
}
