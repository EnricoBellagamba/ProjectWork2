package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.test.*;
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
public class TentativoTestController {

    private final TestService testService;
    private final DomandaRepository domandaRepository;
    private final OpzioneRepository opzioneRepository;
    private final TentativoTestRepository tentativoTestRepository;
    private final RispostaRepository rispostaRepository;
    private final CandidaturaRepository candidaturaRepository;

    public TentativoTestController(
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
    //           STORICO TENTATIVI
    // =========================================================

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
}
