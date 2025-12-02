package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.model.TipoTest;
import com.example.ProjectWork.repository.TestRepository;
import com.example.ProjectWork.repository.TipoTestRepository;
import com.example.ProjectWork.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TipoTestRepository tipoTestRepository;

    public TestServiceImpl(TestRepository testRepository,
                           TipoTestRepository tipoTestRepository) {
        this.testRepository = testRepository;
        this.tipoTestRepository = tipoTestRepository;
    }

    /**
     * Restituisce tutti i test presenti nel DB.
     */
    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    /**
     * Restituisce un test dato il suo ID, oppure errore se non esiste.
     */
    @Override
    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test non trovato con ID: " + id));
    }

    /**
     * Creazione di un nuovo test.
     * Applica default e vincoli di business:
     * - durataMinuti: 1..60
     * - numeroDomande: 1..20
     * - punteggioMin: default 0, non negativo
     * - punteggioMax: default 100, massimo 100
     */
    @Override
    public Test createTest(Test newTest) {
        applyDefaultsAndValidate(newTest);
        return testRepository.save(newTest);
    }

    /**
     * Aggiornamento dei campi base del test e del TipoTest associato.
     * Applica gli stessi vincoli di business della create.
     */
    @Override
    public Test updateTest(Long id, Test testDetails) {
        Test existingTest = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test non trovato con id: " + id));

        existingTest.setTitolo(testDetails.getTitolo());
        existingTest.setDescrizione(testDetails.getDescrizione());
        existingTest.setDurataMinuti(testDetails.getDurataMinuti());
        existingTest.setNumeroDomande(testDetails.getNumeroDomande());
        existingTest.setPunteggioMin(testDetails.getPunteggioMin());
        existingTest.setAttivo(testDetails.getAttivo());

        // Relazione con TipoTest
        if (testDetails.getTipoTest() != null) {
            Long tipoTestId = testDetails.getTipoTest().getIdTipoTest();
            TipoTest tipoTest = tipoTestRepository.findById(tipoTestId)
                    .orElseThrow(() -> new RuntimeException("TipoTest non trovato con id: " + tipoTestId));
            existingTest.setTipoTest(tipoTest);
        } else {
            existingTest.setTipoTest(null);
        }

        // Applica default (punteggi) e vincoli (durata, domande, punteggi)
        applyDefaultsAndValidate(existingTest);

        return testRepository.save(existingTest);
    }

    /**
     * Eliminazione test.
     */
    @Override
    public void deleteTest(Long id) {
        if (!testRepository.existsById(id)) {
            throw new RuntimeException("Test con ID " + id + " non trovato");
        }
        testRepository.deleteById(id);
    }

    // =====================================================================
    //                 LOGICA DI BUSINESS CONSTRAINTS
    // =====================================================================

    /**
     * Applica i default sui punteggi e valida i vincoli di business.
     *
     * Regole:
     * - durataMinuti: obbligatoria, 1..60
     * - numeroDomande: obbligatorio, 1..20
     * - punteggioMin: se null -> 0; deve essere >= 0
     * - punteggioMax: se null -> 100; deve essere <= 100
     * - opzionale ma sensato: punteggioMax >= punteggioMin
     */
    private void applyDefaultsAndValidate(Test test) {
        Integer durata = test.getDurataMinuti();
        Integer numeroDomande = test.getNumeroDomande();
        Integer pMin = test.getPunteggioMin();

        // Default punteggio minimo: 0
        if (pMin == null || pMin < 0) {
            pMin = 0;
            test.setPunteggioMin(pMin);
        }

        validateTestBusinessRules(durata, numeroDomande, pMin);
    }

    /**
     * Effettua i controlli "hard" sui valori.
     */
    private void validateTestBusinessRules(Integer durataMinuti,
                                           Integer numeroDomande,
                                           Integer punteggioMin) {

        // Durata obbligatoria, tra 1 e 60
        if (durataMinuti == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La durata del test (durataMinuti) è obbligatoria"
            );
        }
        if (durataMinuti <= 0 || durataMinuti > 60) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La durata del test deve essere compresa tra 1 e 60 minuti"
            );
        }

        // Numero domande obbligatorio, tra 1 e 20
        if (numeroDomande == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il numero di domande (numeroDomande) è obbligatorio"
            );
        }
        if (numeroDomande <= 0 || numeroDomande > 20) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il numero di domande deve essere compreso tra 1 e 20"
            );
        }

        // Punteggio minimo >= 0
        if (punteggioMin == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il punteggio minimo (punteggioMin) non può essere null"
            );
        }
        if (punteggioMin < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il punteggio minimo non può essere negativo (minimo 0)"
            );
        }
    }
}
