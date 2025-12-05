package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Test;

import java.util.List;

/**
 * Servizio applicativo per la gestione dei Test.
 * Espone le operazioni CRUD di base utilizzate dai controller HR e candidato.
 */
public interface TestService {

    /**
     * Restituisce la lista di tutti i test presenti a sistema.
     */
    List<Test> getAllTests();

    /**
     * Restituisce il test con l'id specificato.
     *
     * @param id identificativo del test
     * @return entità Test
     * @throws RuntimeException se il test non viene trovato
     */
    Test getTestById(Long id);

    /**
     * Crea un nuovo test.
     * Applica i vincoli di business su durata, numero domande e punteggi.
     *
     * @param newTest entità Test da creare
     * @return Test creato e salvato
     */
    Test createTest(Test newTest);

    /**
     * Aggiorna un test esistente.
     * Applica i vincoli di business su durata, numero domande e punteggi.
     *
     * @param id   id del test da aggiornare
     * @param test dati aggiornati
     * @return Test aggiornato
     * @throws RuntimeException se il test non viene trovato
     */
    Test updateTest(Long id, Test test);


    void deleteTest(Long idTest);
}
