package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.model.TipoTest;
import com.example.ProjectWork.repository.TestRepository;
import com.example.ProjectWork.repository.TipoTestRepository;
import com.example.ProjectWork.service.TestService;
import org.springframework.stereotype.Service;

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
     */
    @Override
    public Test createTest(Test newTest) {
        return testRepository.save(newTest);
    }

    /**
     * Aggiornamento dei campi base del test e del TipoTest associato.
     */
    @Override
    public Test updateTest(Long id, Test testDetails) {
        Test existingTest = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test non trovato con id: " + id));

        existingTest.setTitolo(testDetails.getTitolo());
        existingTest.setDescrizione(testDetails.getDescrizione());
        existingTest.setDurataMinuti(testDetails.getDurataMinuti());
        existingTest.setNumeroDomande(testDetails.getNumeroDomande());
        existingTest.setPunteggioMax(testDetails.getPunteggioMax());

        Integer pMin = (testDetails.getPunteggioMin() != null)
                ? testDetails.getPunteggioMin()
                : 0;
        existingTest.setPunteggioMin(pMin);

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
}
