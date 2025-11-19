package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Domanda;
import com.example.ProjectWork.model.Opzione;
import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.repository.DomandaRepository;
import com.example.ProjectWork.repository.OpzioneRepository;
import com.example.ProjectWork.service.TestService;
import com.example.ProjectWork.dto.test.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;
    private final DomandaRepository domandaRepository;
    private final OpzioneRepository opzioneRepository;

    public TestController(
            TestService testService,
            DomandaRepository domandaRepository,
            OpzioneRepository opzioneRepository
    ) {
        this.testService = testService;
        this.domandaRepository = domandaRepository;
        this.opzioneRepository = opzioneRepository;
    }

    // =========================================================
    //               CRUD BASE TEST
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Test> getTestById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(testService.getTestById(id));
    }

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test created = testService.createTest(test);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Test> updateTest(
            @PathVariable Long id,
            @RequestBody Test test
    ) {
        return ResponseEntity.ok(testService.updateTest(id, test));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    //        TEST DISPONIBILI PER CANDIDATO
    // =========================================================

    /**
     * Lista test "disponibili" per il frontend candidati.
     * URL: GET /api/test/disponibili
     */
    @GetMapping("/disponibili")
    public ResponseEntity<List<TestListItemDto>> getTestDisponibili() {
        List<Test> tests = testService.getAllTests();

        List<TestListItemDto> risultato = tests.stream()
                .map(t -> new TestListItemDto(
                        t.getIdTest(),
                        t.getTitolo(),
                        null, // tipo test opzionale, per ora non usato
                        t.getDurataMinuti(),
                        t.getDescrizione(),
                        t.getPunteggioMax()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(risultato);
    }

    // =========================================================
    //         STRUTTURA COMPLETA DI UN TEST (INTRO)
    // =========================================================

    /**
     * Endpoint legacy già presente:
     * URL: GET /api/test/{id}/domande
     */
    @GetMapping("/{id}/domande")
    public ResponseEntity<StrutturaTestResponse> getStrutturaTestLegacy(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(buildStrutturaTestResponse(id));
    }

    /**
     * Endpoint usato dal frontend per la pagina di introduzione:
     * URL: GET /api/test/{id}/struttura
     */
    @GetMapping("/{id}/struttura")
    public ResponseEntity<StrutturaTestResponse> getStrutturaTest(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(buildStrutturaTestResponse(id));
    }

    // ---------------------------------------------------------
    // Helper interno per montare la struttura completa del test
    // ---------------------------------------------------------
    private StrutturaTestResponse buildStrutturaTestResponse(Long idTest) {
        Test test = testService.getTestById(idTest);

        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream()
                .map(domanda -> {
                    List<Opzione> opzioni = opzioneRepository
                            .findByDomanda_IdDomanda(domanda.getIdDomanda());

                    List<OpzioneDto> opzioneDtos = opzioni.stream()
                            .map(opzione -> new OpzioneDto(
                                    opzione.getIdOpzione(),
                                    opzione.getTestoOpzione()
                            ))
                            .collect(Collectors.toList());

                    return new DomandaDto(
                            domanda.getIdDomanda(),
                            domanda.getTesto(),
                            opzioneDtos
                    );
                })
                .collect(Collectors.toList());

        String tipo = null; // se vuoi, puoi derivarlo da test.getTipoTest()

        int numeroDomandeReali = domandaDtos.size();

        return new StrutturaTestResponse(
                test.getIdTest(),
                test.getTitolo(),
                test.getDescrizione(),
                test.getDurataMinuti(),
                numeroDomandeReali,
                test.getPunteggioMax(),
                test.getPunteggioMin(),
                tipo,
                domandaDtos
        );
    }
}
