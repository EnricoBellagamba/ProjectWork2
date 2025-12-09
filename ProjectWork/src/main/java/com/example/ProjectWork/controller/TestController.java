package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.test.*;
import com.example.ProjectWork.model.Domanda;
import com.example.ProjectWork.model.Opzione;
import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.model.TipoTest;
import com.example.ProjectWork.repository.DomandaRepository;
import com.example.ProjectWork.repository.OpzioneRepository;
import com.example.ProjectWork.repository.TipoTestRepository;
import com.example.ProjectWork.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;
    private final DomandaRepository domandaRepository;
    private final OpzioneRepository opzioneRepository;
    private final TipoTestRepository tipoTestRepository;

    public TestController(
            TestService testService,
            DomandaRepository domandaRepository,
            OpzioneRepository opzioneRepository,
            TipoTestRepository tipoTestRepository
    ) {
        this.testService = testService;
        this.domandaRepository = domandaRepository;
        this.opzioneRepository = opzioneRepository;
        this.tipoTestRepository = tipoTestRepository;
    }

    // =====================================================================
    //                         CRUD BASE
    // =====================================================================

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

    // =====================================================================
    //      CREAZIONE COMPLETA TEST + DOMANDE + OPZIONI (CON TIPO TEST)
    // =====================================================================

    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody TestCreateRequest req) {

        // -------------------------
        // 1) Valido e recupero il TipoTest
        // -------------------------
        if (req.codiceTipoTest == null || req.codiceTipoTest.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "codiceTipoTest è obbligatorio"
            );
        }

        TipoTest tipoTest = tipoTestRepository.findByCodice(req.codiceTipoTest)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "TipoTest non valido: " + req.codiceTipoTest
                ));

        // -------------------------
        // 2) NORMALIZZO I VALORI NUMERICI DAL DTO
        //    (paracadute extra prima di arrivare alla service)
        // -------------------------

        // numeroDomande li validiamo in service,
        // ma se proprio arrivano null lanciamo errore chiaro qui
        if (req.numeroDomande == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "numeroDomande è obbligatorio"
            );
        }

        // Controlla che il numero di domande fornite corrisponda a numeroDomande
        if (req.domande == null || req.domande.size() != req.numeroDomande) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il numero di domande fornite (" + (req.domande == null ? 0 : req.domande.size()) +
                            ") deve corrispondere al campo numeroDomande (" + req.numeroDomande + ")."
            );
        }

        // Calcola il Punteggio Totale Massimale dalle Domande
        int punteggioTotaleDomande = 0;
        // Punteggio massimo consentito per singola domanda (come da commento nel tuo DTO)
        final int MAX_PUNTI_PER_DOMANDA = 10;

        for (TestCreateRequest.DomandaCreateRequest d : req.domande) {

            // Vincolo: il punteggio di ogni domanda deve essere valido (tra 1 e 10)
            if (d.punteggio == null || d.punteggio <= 0 || d.punteggio > MAX_PUNTI_PER_DOMANDA) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ogni domanda deve avere un punteggio compreso tra 1 e " + MAX_PUNTI_PER_DOMANDA + ". Trovato: " + d.punteggio
                );
            }

            // Somma i punteggi di tutte le domande per ottenere il Punteggio Massimo RAGGIUNGIBILE
            punteggioTotaleDomande += d.punteggio;
        }

        // -------------------------
        // 3) CREA TEST BASE
        //    (i vincoli su durata, domande e punteggi li applica poi il service)
        // -------------------------
        Test test = new Test();
        test.setTitolo(req.titolo);
        test.setDescrizione(req.descrizione);
        test.setDurataMinuti(req.durataMinuti);
        test.setNumeroDomande(req.numeroDomande);
        test.setPunteggioMin(req.punteggioMin);

        // imposta il tipo test scelto
        test.setTipoTest(tipoTest);

        // nel model il setter si chiama setAttivo(Boolean)
        test.setAttivo(true);

        // Questo applica default (punteggi) + vincoli (durata, domande, punteggi)
        Test savedTest = testService.createTest(test);

        // -------------------------
        // 4) CREA DOMANDE + OPZIONI
        // -------------------------
        if (req.domande != null) {
            for (TestCreateRequest.DomandaCreateRequest d : req.domande) {

                Domanda domanda = new Domanda();
                domanda.setTest(savedTest);
                domanda.setTesto(d.testo);

                Domanda savedDomanda = domandaRepository.save(domanda);

                if (d.opzioni != null) {
                    for (TestCreateRequest.OpzioneCreateRequest o : d.opzioni) {

                        Opzione opzione = new Opzione();
                        opzione.setDomanda(savedDomanda);
                        opzione.setTestoOpzione(o.testoOpzione);
                        // nel model il setter è setIsCorretta(Boolean)
                        opzione.setIsCorretta(o.corretta);

                        opzioneRepository.save(opzione);
                    }
                }
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(savedTest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Test> updateTest(
            @PathVariable Long id,
            @RequestBody Test test
    ) {
        return ResponseEntity.ok(testService.updateTest(id, test));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        testService.deleteTest(id);
        return ResponseEntity.noContent().build();
    }

    // =====================================================================
    //                 LISTA TEST DISPONIBILI PER CANDIDATO
    // =====================================================================

    @GetMapping("/disponibili")
    public ResponseEntity<List<TestListItemDto>> getTestDisponibili() {
        List<Test> tests = testService.getAllTests();

        List<TestListItemDto> risultato = tests.stream()
                .map(t -> new TestListItemDto(
                        t.getIdTest(),
                        t.getTitolo(),
                        t.getTipoTest() != null ? t.getTipoTest().getCodice() : null,
                        t.getDurataMinuti(),
                        t.getDescrizione()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(risultato);
    }

    // =====================================================================
    //                 STRUTTURA COMPLETA TEST (INTRO + DOMANDE)
    // =====================================================================

    @GetMapping("/{id}/domande")
    public ResponseEntity<StrutturaTestResponse> getStrutturaTestLegacy(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(buildStrutturaTestResponse(id));
    }

    @GetMapping("/{id}/struttura")
    public ResponseEntity<StrutturaTestResponse> getStrutturaTest(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(buildStrutturaTestResponse(id));
    }

    // =====================================================================
    //                    COSTRUZIONE STRUTTURA COMPLETA
    // =====================================================================

    private StrutturaTestResponse buildStrutturaTestResponse(Long idTest) {

        Test test = testService.getTestById(idTest);
        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream()
                .map(domanda -> {

                    List<Opzione> opzioni =
                            opzioneRepository.findByDomanda_IdDomanda(domanda.getIdDomanda());

                    List<OpzioneDto> opzioneDtos = opzioni.stream()
                            .map(o -> new OpzioneDto(
                                    o.getIdOpzione(),
                                    o.getTestoOpzione(),
                                    o.getIsCorretta()
                            ))

                            .collect(Collectors.toList());

                    return new DomandaDto(
                            domanda.getIdDomanda(),
                            domanda.getTesto(),
                            opzioneDtos
                    );
                })
                .collect(Collectors.toList());

        int numeroDomandeReali = domandaDtos.size();

        String tipoCodice = test.getTipoTest() != null
                ? test.getTipoTest().getCodice()
                : null;

        return new StrutturaTestResponse(
                test.getIdTest(),
                test.getTitolo(),
                test.getDescrizione(),
                test.getDurataMinuti(),
                numeroDomandeReali,
                test.getPunteggioMin(),
                tipoCodice,
                domandaDtos
        );
    }
}
