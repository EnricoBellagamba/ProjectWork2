package com.example.ProjectWork.controller;

import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Domanda;
import com.example.ProjectWork.model.Opzione;
import com.example.ProjectWork.model.Risposta;
import com.example.ProjectWork.model.TentativoTest;
import com.example.ProjectWork.model.Test;
import com.example.ProjectWork.repository.CandidaturaRepository;
import com.example.ProjectWork.repository.DomandaRepository;
import com.example.ProjectWork.repository.OpzioneRepository;
import com.example.ProjectWork.repository.RispostaRepository;
import com.example.ProjectWork.repository.TentativoTestRepository;
import com.example.ProjectWork.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final TestService testService;
    private final DomandaRepository domandaRepository;
    private final OpzioneRepository opzioneRepository;
    private final TentativoTestRepository tentativoTestRepository;
    private final RispostaRepository rispostaRepository;
    private final CandidaturaRepository candidaturaRepository;

    public TestController(
            TestService testService,
            DomandaRepository domandaRepository,
            OpzioneRepository opzioneRepository,
            TentativoTestRepository tentativoTestRepository,
            RispostaRepository rispostaRepository,
            CandidaturaRepository candidaturaRepository
    ) {
        this.testService = testService;
        this.domandaRepository = domandaRepository;
        this.opzioneRepository = opzioneRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.rispostaRepository = rispostaRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    // =========================================================
    //               CRUD BASE ESISTENTE
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        return ResponseEntity.ok(testService.getAllTests());
    }

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

    /**
     * Storico tentativi del candidato.
     * Per ora ritorna tutti i tentativi presenti a DB (senza filtrare per utente).
     *
     * URL: GET /api/test/tentativi/miei
     */
    @GetMapping("/tentativi/miei")
    public ResponseEntity<List<TentativoListItemDto>> getTentativiMiei() {
        List<TentativoTest> tentativi = tentativoTestRepository.findAll();

        List<TentativoListItemDto> risultato = tentativi.stream()
                .map(t -> {
                    Test test = t.getIdTest();
                    Long idTest = test != null ? test.getIdTest() : null;
                    String titoloTest = test != null ? test.getTitolo() : null;
                    Integer durataMinuti = test != null ? test.getDurataMinuti() : null;
                    Integer punteggioMax = test != null ? test.getPunteggioMax() : null;

                    String esitoCodice = "IN_VALUTAZIONE";
                    if (t.getIdEsitoTentativo() != null && t.getIdEsitoTentativo().getCodice() != null) {
                        esitoCodice = t.getIdEsitoTentativo().getCodice();
                    }

                    String completatoAt = t.getCompletatoAt() != null
                            ? t.getCompletatoAt().toString()
                            : null;

                    return new TentativoListItemDto(
                            t.getIdTentativo(),
                            idTest,
                            titoloTest,
                            durataMinuti,
                            t.getPunteggioTotale(),
                            punteggioMax,
                            esitoCodice,
                            completatoAt
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(risultato);
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

        String tipo = null;
        // se vuoi, puoi derivarlo da test.getTipoTest()

        int numeroDomandeReali = domandaDtos.size();

        return new StrutturaTestResponse(
                test.getIdTest(),
                test.getTitolo(),
                test.getDescrizione(),
                test.getDurataMinuti(),
                numeroDomandeReali,          // <-- usa il numero reale di domande
                test.getPunteggioMax(),
                test.getPunteggioMin(),
                tipo,
                domandaDtos
        );
    }

    // =========================================================
    //           TENTATIVI: AVVIO, DOMANDE, RISPOSTE
    // =========================================================

    /**
     * Avvia un nuovo tentativo per il test indicato.
     * URL: POST /api/test/{idTest}/tentativi/avvia
     *
     * Il frontend chiama avviaTest(idTest).
     */
    @PostMapping("/{idTest}/tentativi/avvia")
    public ResponseEntity<AvviaTestResponse> avviaTest(
            @PathVariable Long idTest,
            @RequestBody(required = false) AvviaTestRequest request
    ) {
        if (request == null) {
            request = new AvviaTestRequest();
        }

        Test test = testService.getTestById(idTest);

        // Prendiamo una candidatura "di comodo" esistente,
        // giusto per rispettare il vincolo NOT NULL su idCandidatura.
        Candidatura candidatura = candidaturaRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Nessuna candidatura presente a DB: inserisci almeno una riga in CANDIDATURA per poter avviare i test.")
                );

        TentativoTest tentativo = new TentativoTest();
        tentativo.setIdTest(test);
        tentativo.setIdCandidatura(candidatura);
        tentativo.setIniziatoAt(LocalDate.now());
        tentativo.setCompletatoAt(null);
        tentativo.setPunteggioTotale(0);

        TentativoTest salvato = tentativoTestRepository.save(tentativo);

        AvviaTestResponse response = new AvviaTestResponse(
                salvato.getIdTentativo(),
                test.getIdTest(),
                salvato.getIniziatoAt().toString()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Restituisce domande e opzioni per un tentativo specifico.
     * URL: GET /api/test/tentativi/{idTentativo}/domande
     */
    @GetMapping("/tentativi/{idTentativo}/domande")
    public ResponseEntity<GetDomandeResponse> getDomandeTentativo(
            @PathVariable Long idTentativo
    ) {
        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato con id: " + idTentativo));

        Test test = tentativo.getIdTest();

        List<Domanda> domande = domandaRepository.findByTest_IdTest(test.getIdTest());

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

        GetDomandeResponse response = new GetDomandeResponse(
                tentativo.getIdTentativo(),
                test.getIdTest(),
                test.getTitolo(),
                test.getDurataMinuti(),
                domandaDtos
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Salva tutte le risposte di un tentativo e calcola il punteggio.
     * URL: POST /api/test/tentativi/{idTentativo}/risposte
     */
    @PostMapping("/tentativi/{idTentativo}/risposte")
    public ResponseEntity<InviaRisposteResponse> inviaRisposte(
            @PathVariable Long idTentativo,
            @RequestBody InviaRisposteRequest request
    ) {
        if (request == null) {
            throw new RuntimeException("Request vuota");
        }

        request.setIdTentativo(idTentativo);

        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato con id: " + idTentativo));

        Test test = tentativo.getIdTest();

        // cancella eventuali risposte precedenti
        List<Risposta> precedenti = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        if (!precedenti.isEmpty()) {
            rispostaRepository.deleteAll(precedenti);
        }

        // numero domande reali del test
        List<Domanda> domandeTest = domandaRepository.findByTest_IdTest(test.getIdTest());
        int numeroDomandeEffettive = domandeTest.size() > 0 ? domandeTest.size() : 1;

        int punteggioPerDomanda = test.getPunteggioMax() / numeroDomandeEffettive;

        int punteggioTotale = 0;

        for (InviaRisposteRequest.RispostaInput input : request.getRisposte()) {
            Domanda domanda = domandaRepository.findById(input.getIdDomanda())
                    .orElseThrow(() -> new RuntimeException("Domanda non trovata con id: " + input.getIdDomanda()));

            Opzione opzione = null;
            if (input.getIdOpzione() != null) {
                opzione = opzioneRepository.findById(input.getIdOpzione())
                        .orElseThrow(() -> new RuntimeException("Opzione non trovata con id: " + input.getIdOpzione()));
            }

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());
            Integer punteggioAssegnato = corretta ? punteggioPerDomanda : 0;

            if (corretta) {
                punteggioTotale += punteggioPerDomanda;
            }

            Risposta risposta = new Risposta(
                    punteggioAssegnato,
                    tentativo,
                    domanda,
                    opzione
            );

            rispostaRepository.save(risposta);
        }

        tentativo.setPunteggioTotale(punteggioTotale);
        tentativo.setCompletatoAt(LocalDate.now());
        // volendo qui potresti settare idEsitoTentativo in base a SUPERATO/NON_SUPERATO
        tentativoTestRepository.save(tentativo);

        String esitoCodice;
        if (punteggioTotale >= test.getPunteggioMin()) {
            esitoCodice = "SUPERATO";
        } else {
            esitoCodice = "NON_SUPERATO";
        }

        InviaRisposteResponse response = new InviaRisposteResponse(
                tentativo.getIdTentativo(),
                tentativo.getPunteggioTotale(),
                test.getPunteggioMax(),
                esitoCodice
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Restituisce il riepilogo dei risultati di un tentativo.
     * URL: GET /api/test/tentativi/{idTentativo}/risultati
     */
    @GetMapping("/tentativi/{idTentativo}/risultati")
    public ResponseEntity<RisultatoTentativoDettaglioDto> getRisultatoTentativo(
            @PathVariable Long idTentativo
    ) {
        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato con id: " + idTentativo));

        Test test = tentativo.getIdTest();

        List<Risposta> risposte = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);

        // numero reale di domande del test
        int numeroDomande = domandaRepository.findByTest_IdTest(test.getIdTest()).size();

        int numeroCorrette = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null
                        && Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        int numeroErrate = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null
                        && !Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        int numeroNonRisposte = numeroDomande - numeroCorrette - numeroErrate;
        if (numeroNonRisposte < 0) {
            numeroNonRisposte = 0;
        }

        String esitoCodice = "IN_VALUTAZIONE";
        if (tentativo.getIdEsitoTentativo() != null && tentativo.getIdEsitoTentativo().getCodice() != null) {
            esitoCodice = tentativo.getIdEsitoTentativo().getCodice();
        }

        String completatoAt = tentativo.getCompletatoAt() != null
                ? tentativo.getCompletatoAt().toString()
                : null;

        Integer durataUsataMinuti = null;

        RisultatoTentativoDettaglioDto dto = new RisultatoTentativoDettaglioDto(
                tentativo.getIdTentativo(),
                test.getIdTest(),
                test.getTitolo(),
                tentativo.getPunteggioTotale(),
                test.getPunteggioMax(),
                test.getPunteggioMin(),
                esitoCodice,
                completatoAt,
                durataUsataMinuti,
                numeroDomande,
                numeroCorrette,
                numeroErrate,
                numeroNonRisposte
        );

        return ResponseEntity.ok(dto);
    }

    // =========================================================
    //                      DTO INTERNI
    // =========================================================

    // ---- Lista test ----
    public static class TestListItemDto {
        private Long idTest;
        private String titolo;
        private String tipo; // opzionale
        private Integer durataMinuti;
        private String descrizione;
        private Integer punteggioMax;

        public TestListItemDto(
                Long idTest,
                String titolo,
                String tipo,
                Integer durataMinuti,
                String descrizione,
                Integer punteggioMax
        ) {
            this.idTest = idTest;
            this.titolo = titolo;
            this.tipo = tipo;
            this.durataMinuti = durataMinuti;
            this.descrizione = descrizione;
            this.punteggioMax = punteggioMax;
        }

        public Long getIdTest() {
            return idTest;
        }

        public String getTitolo() {
            return titolo;
        }

        public String getTipo() {
            return tipo;
        }

        public Integer getDurataMinuti() {
            return durataMinuti;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public Integer getPunteggioMax() {
            return punteggioMax;
        }
    }

    // ---- Storico tentativi ----
    public static class TentativoListItemDto {
        private Long idTentativo;
        private Long idTest;
        private String titoloTest;
        private Integer durataMinuti;
        private Integer punteggioTotale;
        private Integer punteggioMax;
        private String esito;
        private String completatoAt;

        public TentativoListItemDto(
                Long idTentativo,
                Long idTest,
                String titoloTest,
                Integer durataMinuti,
                Integer punteggioTotale,
                Integer punteggioMax,
                String esito,
                String completatoAt
        ) {
            this.idTentativo = idTentativo;
            this.idTest = idTest;
            this.titoloTest = titoloTest;
            this.durataMinuti = durataMinuti;
            this.punteggioTotale = punteggioTotale;
            this.punteggioMax = punteggioMax;
            this.esito = esito;
            this.completatoAt = completatoAt;
        }

        public Long getIdTentativo() {
            return idTentativo;
        }

        public Long getIdTest() {
            return idTest;
        }

        public String getTitoloTest() {
            return titoloTest;
        }

        public Integer getDurataMinuti() {
            return durataMinuti;
        }

        public Integer getPunteggioTotale() {
            return punteggioTotale;
        }

        public Integer getPunteggioMax() {
            return punteggioMax;
        }

        public String getEsito() {
            return esito;
        }

        public String getCompletatoAt() {
            return completatoAt;
        }
    }

    // ---- Struttura test / domande / opzioni ----

    public static class OpzioneDto {
        private Long idOpzione;
        private String testoOpzione;

        public OpzioneDto(Long idOpzione, String testoOpzione) {
            this.idOpzione = idOpzione;
            this.testoOpzione = testoOpzione;
        }

        public Long getIdOpzione() {
            return idOpzione;
        }

        public String getTestoOpzione() {
            return testoOpzione;
        }
    }

    public static class DomandaDto {
        private Long idDomanda;
        private String testo;
        private List<OpzioneDto> opzioni;

        public DomandaDto(Long idDomanda, String testo, List<OpzioneDto> opzioni) {
            this.idDomanda = idDomanda;
            this.testo = testo;
            this.opzioni = opzioni;
        }

        public Long getIdDomanda() {
            return idDomanda;
        }

        public String getTesto() {
            return testo;
        }

        public List<OpzioneDto> getOpzioni() {
            return opzioni;
        }
    }

    public static class StrutturaTestResponse {
        private Long idTest;
        private String titolo;
        private String descrizione;
        private Integer durataMinuti;
        private Integer numeroDomande;
        private Integer punteggioMax;
        private Integer punteggioMin;
        private String tipo;
        private List<DomandaDto> domande;

        public StrutturaTestResponse(
                Long idTest,
                String titolo,
                String descrizione,
                Integer durataMinuti,
                Integer numeroDomande,
                Integer punteggioMax,
                Integer punteggioMin,
                String tipo,
                List<DomandaDto> domande
        ) {
            this.idTest = idTest;
            this.titolo = titolo;
            this.descrizione = descrizione;
            this.durataMinuti = durataMinuti;
            this.numeroDomande = numeroDomande;
            this.punteggioMax = punteggioMax;
            this.punteggioMin = punteggioMin;
            this.tipo = tipo;
            this.domande = domande;
        }

        public Long getIdTest() {
            return idTest;
        }

        public String getTitolo() {
            return titolo;
        }

        public String getDescrizione() {
            return descrizione;
        }

        public Integer getDurataMinuti() {
            return durataMinuti;
        }

        public Integer getNumeroDomande() {
            return numeroDomande;
        }

        public Integer getPunteggioMax() {
            return punteggioMax;
        }

        public Integer getPunteggioMin() {
            return punteggioMin;
        }

        public String getTipo() {
            return tipo;
        }

        public List<DomandaDto> getDomande() {
            return domande;
        }
    }

    // ---- Avvio test ----

    public static class AvviaTestRequest {
        // per ora vuoto, in futuro puoi aggiungere idPosizione, ecc.
    }

    public static class AvviaTestResponse {
        private Long idTentativo;
        private Long idTest;
        private String iniziatoAt;

        public AvviaTestResponse(Long idTentativo, Long idTest, String iniziatoAt) {
            this.idTentativo = idTentativo;
            this.idTest = idTest;
            this.iniziatoAt = iniziatoAt;
        }

        public Long getIdTentativo() {
            return idTentativo;
        }

        public Long getIdTest() {
            return idTest;
        }

        public String getIniziatoAt() {
            return iniziatoAt;
        }
    }

    // ---- Domande per tentativo ----

    public static class GetDomandeResponse {
        private Long idTentativo;
        private Long idTest;
        private String titoloTest;
        private Integer durataMinuti;
        private List<DomandaDto> domande;

        public GetDomandeResponse(
                Long idTentativo,
                Long idTest,
                String titoloTest,
                Integer durataMinuti,
                List<DomandaDto> domande
        ) {
            this.idTentativo = idTentativo;
            this.idTest = idTest;
            this.titoloTest = titoloTest;
            this.durataMinuti = durataMinuti;
            this.domande = domande;
        }

        public Long getIdTentativo() {
            return idTentativo;
        }

        public Long getIdTest() {
            return idTest;
        }

        public String getTitoloTest() {
            return titoloTest;
        }

        public Integer getDurataMinuti() {
            return durataMinuti;
        }

        public List<DomandaDto> getDomande() {
            return domande;
        }
    }

    // ---- Invio risposte ----

    public static class InviaRisposteRequest {
        private Long idTentativo;
        private List<RispostaInput> risposte;

        public InviaRisposteRequest() {
        }

        public Long getIdTentativo() {
            return idTentativo;
        }

        public void setIdTentativo(Long idTentativo) {
            this.idTentativo = idTentativo;
        }

        public List<RispostaInput> getRisposte() {
            return risposte;
        }

        public void setRisposte(List<RispostaInput> risposte) {
            this.risposte = risposte;
        }

        public static class RispostaInput {
            private Long idDomanda;
            private Long idOpzione; // può essere null

            public RispostaInput() {
            }

            public Long getIdDomanda() {
                return idDomanda;
            }

            public void setIdDomanda(Long idDomanda) {
                this.idDomanda = idDomanda;
            }

            public Long getIdOpzione() {
                return idOpzione;
            }

            public void setIdOpzione(Long idOpzione) {
                this.idOpzione = idOpzione;
            }
        }
    }

    public static class InviaRisposteResponse {
        private Long idTentativo;
        private Integer punteggioTotale;
        private Integer punteggioMax;
        private String esito;

        public InviaRisposteResponse(
                Long idTentativo,
                Integer punteggioTotale,
                Integer punteggioMax,
                String esito
        ) {
            this.idTentativo = idTentativo;
            this.punteggioTotale = punteggioTotale;
            this.punteggioMax = punteggioMax;
            this.esito = esito;
        }

        public Long getIdTentativo() {
            return idTentativo;
        }

        public Integer getPunteggioTotale() {
            return punteggioTotale;
        }

        public Integer getPunteggioMax() {
            return punteggioMax;
        }

        public String getEsito() {
            return esito;
        }
    }

    // ---- Risultato tentativo ----

    public static class RisultatoTentativoDettaglioDto {
        private Long idTentativo;
        private Long idTest;
        private String titoloTest;
        private Integer punteggioTotale;
        private Integer punteggioMax;
        private Integer punteggioMin;
        private String esito;
        private String completatoAt;
        private Integer durataUsataMinuti;
        private Integer numeroDomande;
        private Integer numeroCorrette;
        private Integer numeroErrate;
        private Integer numeroNonRisposte;

        public RisultatoTentativoDettaglioDto(
                Long idTentativo,
                Long idTest,
                String titoloTest,
                Integer punteggioTotale,
                Integer punteggioMax,
                Integer punteggioMin,
                String esito,
                String completatoAt,
                Integer durataUsataMinuti,
                Integer numeroDomande,
                Integer numeroCorrette,
                Integer numeroErrate,
                Integer numeroNonRisposte
        ) {
            this.idTentativo = idTentativo;
            this.idTest = idTest;
            this.titoloTest = titoloTest;
            this.punteggioTotale = punteggioTotale;
            this.punteggioMax = punteggioMax;
            this.punteggioMin = punteggioMin;
            this.esito = esito;
            this.completatoAt = completatoAt;
            this.durataUsataMinuti = durataUsataMinuti;
            this.numeroDomande = numeroDomande;
            this.numeroCorrette = numeroCorrette;
            this.numeroErrate = numeroErrate;
            this.numeroNonRisposte = numeroNonRisposte;
        }

        public Long getIdTentativo() {
            return idTentativo;
        }

        public Long getIdTest() {
            return idTest;
        }

        public String getTitoloTest() {
            return titoloTest;
        }

        public Integer getPunteggioTotale() {
            return punteggioTotale;
        }

        public Integer getPunteggioMax() {
            return punteggioMax;
        }

        public Integer getPunteggioMin() {
            return punteggioMin;
        }

        public String getEsito() {
            return esito;
        }

        public String getCompletatoAt() {
            return completatoAt;
        }

        public Integer getDurataUsataMinuti() {
            return durataUsataMinuti;
        }

        public Integer getNumeroDomande() {
            return numeroDomande;
        }

        public Integer getNumeroCorrette() {
            return numeroCorrette;
        }

        public Integer getNumeroErrate() {
            return numeroErrate;
        }

        public Integer getNumeroNonRisposte() {
            return numeroNonRisposte;
        }
    }
}
