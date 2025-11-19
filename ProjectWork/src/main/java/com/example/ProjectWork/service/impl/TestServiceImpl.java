package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.model.TipoTest;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.TestRepository;
import com.example.ProjectWork.repository.TipoTestRepository;
import com.example.ProjectWork.service.TestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    private final TestRepository testRepository;
    private final TipoTestRepository tipoTestRepository;

    public TestServiceImpl(TestRepository testRepository, TipoTestRepository tipoTestRepository) {
        this.testRepository = testRepository;
        this.tipoTestRepository = tipoTestRepository;
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
        existingTest.setPunteggioMax(testDetails.getPunteggioMax());
        existingTest.setPunteggioMin(testDetails.getPunteggioMin());
        existingTest.setAttivo(testDetails.getAttivo());

        // bisogna aggiornare la relazione many to one con TipoTest
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

    @Override
    public void deleteTest(Long id) {
        if(!testRepository.existsById(id)) {
            throw new RuntimeException("Test con ID " + id + " non trovato");
        }
        testRepository.deleteById(id);
    }

}
