package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.test.AvviaTestRequest;
import com.example.ProjectWork.dto.test.AvviaTestResponse;
import com.example.ProjectWork.dto.test.DomandaDto;
import com.example.ProjectWork.dto.test.GetDomandeResponse;
import com.example.ProjectWork.dto.test.InviaRisposteRequest;
import com.example.ProjectWork.dto.test.InviaRisposteResponse;
import com.example.ProjectWork.dto.test.OpzioneDto;
import com.example.ProjectWork.dto.test.RisultatoTentativoDettaglioDto;
import com.example.ProjectWork.dto.test.StrutturaTestResponse;
import com.example.ProjectWork.dto.test.TentativoListItemDto;
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
                            t.getIdEsitoTentativo() != null
                                    ? t.getIdEsitoTentativo().getCodice()
                                    : "IN_VALUTAZIONE",
                            t.getCompletatoAt() != null
                                    ? t.getCompletatoAt().toString()
                                    : null
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

        if (request == null) {
            request = new AvviaTestRequest();
        }

        Test test = testService.getTestById(idTest);

        // TODO: quando colleghiamo l'utente, useremo la candidatura del candidato loggato.
        // Per ora usiamo la prima candidatura presente a DB.
        Candidatura candidatura = candidaturaRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Inserire almeno una riga in CANDIDATURA."));

        // Una sola esecuzione del test per quella candidatura
        TentativoTest esistente = tentativoTestRepository.findAll().stream()
                .filter(t -> t.getIdTest() != null
                        && t.getIdTest().getIdTest().equals(test.getIdTest())
                        && t.getIdCandidatura() != null
                        && t.getIdCandidatura().getIdCandidatura()
                        .equals(candidatura.getIdCandidatura()))
                .findFirst()
                .orElse(null);

        if (esistente != null) {
            // Test già svolto/avviato → non ne creiamo un altro
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AvviaTestResponse(
                            esistente.getIdTentativo(),
                            test.getIdTest(),
                            esistente.getIniziatoAt() != null
                                    ? esistente.getIniziatoAt().toString()
                                    : null
                    ));
        }

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
                        salvato.getIniziatoAt() != null
                                ? salvato.getIniziatoAt().toString()
                                : null
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

        if (request == null) {
            throw new RuntimeException("Request vuota");
        }

        request.setIdTentativo(idTentativo);

        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new RuntimeException("Tentativo non trovato."));

        Test test = tentativo.getIdTest();

        // Cancella eventuali risposte precedenti per questo tentativo
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

            if (corretta) {
                punteggioTotale += puntiPerDomanda;
            }

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

        InviaRisposteResponse responseBody = new InviaRisposteResponse(
                tentativo.getIdTentativo(),
                punteggioTotale,
                test.getPunteggioMax(),
                esito
        );

        return ResponseEntity.ok(responseBody);
    }

    // =========================================================================
    //                           RISULTATO TENTATIVO
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
                .filter(r -> r.getIdOpzione() != null
                        && Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        int errate = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null
                        && !Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        int nonRisposte = Math.max(0, numeroDomande - corrette - errate);

        String esito = tentativo.getIdEsitoTentativo() == null
                ? "IN_VALUTAZIONE"
                : tentativo.getIdEsitoTentativo().getCodice();

        RisultatoTentativoDettaglioDto dto = new RisultatoTentativoDettaglioDto(
                tentativo.getIdTentativo(),
                test.getIdTest(),
                test.getTitolo(),
                tentativo.getPunteggioTotale(),
                test.getPunteggioMax(),
                test.getPunteggioMin(),
                esito,
                tentativo.getCompletatoAt() != null
                        ? tentativo.getCompletatoAt().toString()
                        : null,
                null,              // durataUsataMinuti (non calcolata)
                numeroDomande,
                corrette,
                errate,
                nonRisposte
        );

        return ResponseEntity.ok(dto);
    }

    // =========================================================================
    //                     STRUTTURA COMPLETA TEST (PER HR)
    // =========================================================================

    @GetMapping("/{idTest}/struttura-tentativo/{idTentativo}")
    public ResponseEntity<StrutturaTestResponse> getStrutturaPerTentativo(
            @PathVariable Long idTest,
            @PathVariable Long idTentativo
    ) {

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

        int numeroDomande = domandaDtos.size();
        Integer punteggioMin = test.getPunteggioMin();
        String tipo = null; // se vuoi puoi prendere descrizione/codice da test.getTipoTest()

        StrutturaTestResponse resp = new StrutturaTestResponse(
                test.getIdTest(),
                test.getTitolo(),
                test.getDescrizione(),
                test.getDurataMinuti(),
                numeroDomande,
                test.getPunteggioMax(),
                punteggioMin,
                tipo,
                domandaDtos
        );

        return ResponseEntity.ok(resp);
    }
}