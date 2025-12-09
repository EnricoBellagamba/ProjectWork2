package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.model.TipoTest;
import com.example.ProjectWork.repository.PosizioneRepository;
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
    private final PosizioneRepository posizioneRepository;

    public TestServiceImpl(TestRepository testRepository,
                           TipoTestRepository tipoTestRepository, PosizioneRepository posizioneRepository) {
        this.testRepository = testRepository;
        this.tipoTestRepository = tipoTestRepository;
        this.posizioneRepository = posizioneRepository;
    }

    @Override
    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    @Override
    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test non trovato con ID: " + id));
    }

    @Override
    public Test createTest(Test newTest) {
        applyDefaultsAndValidate(newTest);
        return testRepository.save(newTest);
    }

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

        if (testDetails.getTipoTest() != null) {
            Long tipoTestId = testDetails.getTipoTest().getIdTipoTest();
            TipoTest tipoTest = tipoTestRepository.findById(tipoTestId)
                    .orElseThrow(() -> new RuntimeException("TipoTest non trovato con id: " + tipoTestId));
            existingTest.setTipoTest(tipoTest);
        } else {
            existingTest.setTipoTest(null);
        }

        applyDefaultsAndValidate(existingTest);

        return testRepository.save(existingTest);
    }

    @Override
    public void deleteTest(Long idTest) {
        Test test = testRepository.findById(idTest)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Test non trovato"));

        boolean associato = posizioneRepository.existsByIdTest(idTest);

        if (associato) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Impossibile eliminare: il test è associato a una o più posizioni."
            );
        }

        testRepository.delete(test);
    }

    // =====================================================================
    //                 LOGICA DI BUSINESS CONSTRAINTS
    // =====================================================================

    private void applyDefaultsAndValidate(Test test) {
        Integer durata = test.getDurataMinuti();
        Integer numeroDomande = test.getNumeroDomande();
        Integer pMin = test.getPunteggioMin();

        if (pMin == null || pMin < 0) {
            pMin = 0;
            test.setPunteggioMin(pMin);
        }

        validateTestBusinessRules(durata, numeroDomande, pMin);
    }

    private void validateTestBusinessRules(Integer durataMinuti,
                                           Integer numeroDomande,
                                           Integer punteggioMin) {

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
