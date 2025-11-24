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

    // =========================================================================
    //                           STORICO TENTATIVI
    // =========================================================================

    @GetMapping("/tentativi/miei")
    public ResponseEntity<List<TentativoListItemDto>> getTentativiMiei() {

        List<TentativoTest> tentativi = tentativoTestRepository.findAll();

        List<TentativoListItemDto> risultato = tentativi.stream()
                .map(t -> {

                    Test test = t.getIdTest();

                    return new TentativoListItemDto(
                            t.getIdTentativo(),
                            test != null ? test.getIdTest() : null,
                            test != null ? test.getTitolo() : null,
                            test != null ? test.getDurataMinuti() : null,
                            t.getPunteggioTotale(),
                            test != null ? test.getPunteggioMax() : null,
                            t.getIdEsitoTentativo() != null ?
                                    t.getIdEsitoTentativo().getCodice() :
                                    "IN_VALUTAZIONE",
                            t.getCompletatoAt() != null ?
                                    t.getCompletatoAt().toString() :
                                    null
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(risultato);
    }

    // =========================================================================
    //                           AVVIO TENTATIVO
    // =========================================================================

    @PostMapping("/{idTest}/tentativi/avvia")
    public ResponseEntity<AvviaTestResponse> avviaTest(
            @PathVariable Long idTest,
            @RequestBody(required = false) AvviaTestRequest request
    ) {

        if (request == null) request = new AvviaTestRequest();

        Test test = testService.getTestById(idTest);

        // pick a candidatura for constraints
        Candidatura candidatura = candidaturaRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inserire almeno una riga in CANDIDATURA."));

        TentativoTest tentativo = new TentativoTest();
        tentativo.setIdTest(test);
        tentativo.setIdCandidatura(candidatura);
        tentativo.setIniziatoAt(LocalDate.now());
        tentativo.setCompletatoAt(null);
        tentativo.setPunteggioTotale(0);

        TentativoTest salvato = tentativoTestRepository.save(tentativo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AvviaTestResponse(
                        salvato.getIdTentativo(),
                        test.getIdTest(),
                        salvato.getIniziatoAt().toString()
                ));
    }

    // =========================================================================
    //                         DOMANDE PER TENTATIVO
    // =========================================================================

    @GetMapping("/tentativi/{idTentativo}/domande")
    public ResponseEntity<GetDomandeResponse> getDomandeTentativo(
            @PathVariable Long idTentativo
    ) {

        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato."));

        Test test = tentativo.getIdTest();

        List<Domanda> domande = domandaRepository.findByTest_IdTest(test.getIdTest());

        List<DomandaDto> domandaDtos = domande.stream()
                .map(domanda -> {
                    List<Opzione> opzioni = opzioneRepository.findByDomanda_IdDomanda(domanda.getIdDomanda());

                    // ************* FIX QUI *************
                    List<OpzioneDto> opzioneDtos = opzioni.stream()
                            .map(o -> new OpzioneDto(
                                    o.getIdOpzione(),
                                    o.getTestoOpzione(),
                                    o.getIsCorretta()      // ORA CORRETTA È PASSATA
                            ))
                            .collect(Collectors.toList());
                    // ************************************

                    return new DomandaDto(
                            domanda.getIdDomanda(),
                            domanda.getTesto(),
                            opzioneDtos
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                new GetDomandeResponse(
                        tentativo.getIdTentativo(),
                        test.getIdTest(),
                        test.getTitolo(),
                        test.getDurataMinuti(),
                        domandaDtos
                )
        );
    }

    // =========================================================================
    //                           INVIO RISPOSTE
    // =========================================================================

    @PostMapping("/tentativi/{idTentativo}/risposte")
    public ResponseEntity<InviaRisposteResponse> inviaRisposte(
            @PathVariable Long idTentativo,
            @RequestBody InviaRisposteRequest request
    ) {

        if (request == null)
            throw new RuntimeException("Request vuota");

        request.setIdTentativo(idTentativo);

        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato."));

        Test test = tentativo.getIdTest();

        // reset old answers
        rispostaRepository.deleteAll(
                rispostaRepository.findByIdTentativo_IdTentativo(idTentativo)
        );

        List<Domanda> domandeTest = domandaRepository.findByTest_IdTest(test.getIdTest());
        int numeroDomande = domandeTest.size() > 0 ? domandeTest.size() : 1;

        int puntiPerDomanda = test.getPunteggioMax() / numeroDomande;
        int punteggioTotale = 0;

        for (InviaRisposteRequest.RispostaInput input : request.getRisposte()) {

            Domanda domanda = domandaRepository.findById(input.getIdDomanda())
                    .orElseThrow(() -> new RuntimeException("Domanda non trovata."));

            Opzione opzione = null;
            if (input.getIdOpzione() != null) {
                opzione = opzioneRepository.findById(input.getIdOpzione())
                        .orElseThrow(() -> new RuntimeException("Opzione non trovata."));
            }

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());

            Integer punteggio = corretta ? puntiPerDomanda : 0;

            if (corretta) punteggioTotale += puntiPerDomanda;

            rispostaRepository.save(
                    new Risposta(punteggio, tentativo, domanda, opzione)
            );
        }

        tentativo.setPunteggioTotale(punteggioTotale);
        tentativo.setCompletatoAt(LocalDate.now());
        tentativoTestRepository.save(tentativo);

        String esito = punteggioTotale >= test.getPunteggioMin()
                ? "SUPERATO"
                : "NON_SUPERATO";

        return ResponseEntity.ok(
                new InviaRisposteResponse(
                        tentativo.getIdTentativo(),
                        punteggioTotale,
                        test.getPunteggioMax(),
                        esito
                )
        );
    }

    // =========================================================================
    //                           RISULTATO
    // =========================================================================

    @GetMapping("/tentativi/{idTentativo}/risultati")
    public ResponseEntity<RisultatoTentativoDettaglioDto> getRisultatoTentativo(
            @PathVariable Long idTentativo
    ) {

        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato."));

        Test test = tentativo.getIdTest();

        List<Risposta> risposte = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);

        int numeroDomande = domandaRepository.findByTest_IdTest(test.getIdTest()).size();

        int corrette = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null &&
                        Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        int errate = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null &&
                        !Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        int nonRisposte = Math.max(0, numeroDomande - corrette - errate);

        String esito = tentativo.getIdEsitoTentativo() == null ?
                "IN_VALUTAZIONE" :
                tentativo.getIdEsitoTentativo().getCodice();

        return ResponseEntity.ok(
                new RisultatoTentativoDettaglioDto(
                        tentativo.getIdTentativo(),
                        test.getIdTest(),
                        test.getTitolo(),
                        tentativo.getPunteggioTotale(),
                        test.getPunteggioMax(),
                        test.getPunteggioMin(),
                        esito,
                        tentativo.getCompletatoAt() != null ?
                                tentativo.getCompletatoAt().toString() :
                                null,
                        null,
                        numeroDomande,
                        corrette,
                        errate,
                        nonRisposte
                )
        );
    }

}
