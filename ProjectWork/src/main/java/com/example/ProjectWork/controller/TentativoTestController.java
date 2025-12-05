package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.test.*;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test/tentativi")
public class TentativoTestController {

    private final TestService testService;
    private final DomandaRepository domandaRepository;
    private final OpzioneRepository opzioneRepository;
    private final TentativoTestRepository tentativoTestRepository;
    private final RispostaRepository rispostaRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final UtenteRepository utenteRepository;
    private final CandidatoRepository candidatoRepository;
    private final TestRepository testRepository;
    private final PosizioneRepository posizioneRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;
    private final EsitoTentativoRepository esitoTentativoRepository;

    public TentativoTestController(
            TestService testService,
            DomandaRepository domandaRepository,
            OpzioneRepository opzioneRepository,
            TentativoTestRepository tentativoTestRepository,
            RispostaRepository rispostaRepository,
            CandidaturaRepository candidaturaRepository,
            UtenteRepository utenteRepository,
            CandidatoRepository candidatoRepository,
            TestRepository testRepository,
            PosizioneRepository posizioneRepository,
            StatoCandidaturaRepository statoCandidaturaRepository,
            EsitoTentativoRepository esitoTentativoRepository
    ) {
        this.testService = testService;
        this.domandaRepository = domandaRepository;
        this.opzioneRepository = opzioneRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.rispostaRepository = rispostaRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
        this.testRepository = testRepository;
        this.posizioneRepository = posizioneRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
        this.esitoTentativoRepository = esitoTentativoRepository;
    }

    // =====================================================================
    // COMPLETA TEST + CREA CANDIDATURA
    // =====================================================================
    @PostMapping("/completa")
    @PreAuthorize("hasRole('CANDIDATO')")
    @Transactional
    public ResponseEntity<InviaRisposteResponse> completaTestECandidatura(
            @RequestBody CompletaTestRequest request,
            Authentication authentication
    ) {
        if (request == null) throw new RuntimeException("Request vuota");

        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseGet(() -> {
                    Candidato nuovo = new Candidato();
                    nuovo.setIdUtente(utente);
                    nuovo.setActive(true);
                    return candidatoRepository.save(nuovo);
                });

        Test test = testService.getTestById(request.getIdTest());
        Posizione posizione = posizioneRepository.findById(request.getIdPosizione())
                .orElseThrow(() -> new RuntimeException("Posizione non trovata"));

        boolean esisteCandidatura = candidaturaRepository
                .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(
                        candidato.getIdCandidato(),
                        request.getIdPosizione()
                );

        if (esisteCandidatura)
            throw new RuntimeException("Hai già una candidatura attiva per questa posizione");

        StatoCandidatura statoIniziale = statoCandidaturaRepository
                .findByCodice("IN_VALUTAZIONE")
                .orElseThrow(() -> new RuntimeException("Stato IN_VALUTAZIONE non trovato"));

        Candidatura candidatura = new Candidatura();
        candidatura.setCandidato(candidato);
        candidatura.setPosizione(posizione);
        candidatura.setStato(statoIniziale);
        candidatura.setCreatedAt(LocalDate.now());

        candidatura = candidaturaRepository.save(candidatura);
        Long idCandidatura = candidatura.getIdCandidatura();

        TentativoTest tentativo = new TentativoTest();
        tentativo.setIdCandidatura(idCandidatura);
        tentativo.setIdTest(test.getIdTest());
        tentativo.setIniziatoAt(request.getIniziatoAt());
        tentativo.setCompletatoAt(LocalDateTime.now());

        TentativoTest tentativoSalvato = tentativoTestRepository.save(tentativo);

        List<Domanda> domandeTest = domandaRepository.findByTest_IdTest(test.getIdTest());
        int punteggioTotale = 0;

        for (CompletaTestRequest.RispostaInput input : request.getRisposte()) {

            Domanda domanda = domandaRepository.findById(input.getIdDomanda())
                    .orElseThrow(() -> new RuntimeException("Domanda non trovata"));

            Opzione opzione = (input.getIdOpzione() != null)
                    ? opzioneRepository.findById(input.getIdOpzione()).orElse(null)
                    : null;

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());
            int punti = corretta ? 1 : 0;

            punteggioTotale += punti;

            rispostaRepository.save(new Risposta(punti, tentativoSalvato, domanda, opzione));
        }

        tentativoSalvato.setPunteggioTotale(punteggioTotale);

        String codiceEsito =
                punteggioTotale >= test.getPunteggioMin() ? "SUPERATO" : "NON_SUPERATO";

        EsitoTentativo esito = esitoTentativoRepository.findByCodice(codiceEsito)
                .orElse(null);

        tentativoSalvato.setIdEsitoTentativo(esito);
        tentativoTestRepository.save(tentativoSalvato);

        // ====================================================
        // AGGIORNAMENTO AUTOMATICO STATO CANDIDATURA
        // ====================================================
        Candidatura candidaturaAggiornamento = candidaturaRepository.findById(idCandidatura)
                .orElseThrow(() -> new RuntimeException("Candidatura non trovata"));

        if ("NON_SUPERATO".equals(codiceEsito)) {
            StatoCandidatura respinta = statoCandidaturaRepository.findByCodice("RESPINTA")
                    .orElseThrow(() -> new RuntimeException("Stato RESPINTA non trovato"));
            candidaturaAggiornamento.setStato(respinta);
        } else {
            StatoCandidatura valutazione = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                    .orElseThrow(() -> new RuntimeException("Stato IN_VALUTAZIONE non trovato"));
            candidaturaAggiornamento.setStato(valutazione);
        }

        candidaturaRepository.save(candidaturaAggiornamento);

        double percentuale =
                domandeTest.isEmpty() ? 0.0 : (punteggioTotale * 100.0) / domandeTest.size();

        return ResponseEntity.ok(
                new InviaRisposteResponse(
                        tentativoSalvato.getIdTentativo(),
                        punteggioTotale,
                        codiceEsito,
                        percentuale
                )
        );
    }

    // =============================================================
    //   STORICO TENTATIVI DELL’UTENTE LOGGATO
    // =============================================================
    @GetMapping("/miei")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<TentativoListItemDto>> getMieiTentativi(Authentication auth) {

        if (auth == null || !auth.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non autenticato");

        Utente utente = utenteRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Candidatura> candidature =
                candidaturaRepository.findByCandidato_IdUtente_IdUtente(utente.getIdUtente());

        if (candidature.isEmpty()) return ResponseEntity.ok(List.of());

        Set<Long> idCandidature = candidature.stream()
                .map(Candidatura::getIdCandidatura)
                .collect(Collectors.toSet());

        List<TentativoTest> completati = tentativoTestRepository.findAll().stream()
                .filter(t -> t.getCompletatoAt() != null &&
                        idCandidature.contains(t.getIdCandidatura()))
                .toList();

        Map<String, TentativoTest> ultimo = new HashMap<>();

        for (TentativoTest t : completati) {
            String key = t.getIdTest() + "#" + t.getIdCandidatura();
            TentativoTest existing = ultimo.get(key);
            if (existing == null ||
                    t.getCompletatoAt().isAfter(existing.getCompletatoAt())) {
                ultimo.put(key, t);
            }
        }

        List<TentativoListItemDto> result = ultimo.values().stream()
                .sorted(Comparator.comparing(TentativoTest::getCompletatoAt).reversed())
                .map(t -> {
                    Test test = testRepository.findById(t.getIdTest()).orElse(null);
                    int numeroDomande = domandaRepository.countByTest_IdTest(t.getIdTest());
                    double percentuale =
                            numeroDomande > 0 ? (t.getPunteggioTotale() * 100.0) / numeroDomande : 0;
                    return new TentativoListItemDto(
                            t.getIdTentativo(),
                            t.getIdTest(),
                            test != null ? test.getTitolo() : "Test #" + t.getIdTest(),
                            test != null ? test.getDurataMinuti() : null,
                            t.getPunteggioTotale(),
                            t.getCodiceEsito(),
                            t.getCompletatoAt(),
                            percentuale
                    );
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    // =============================================================
    //   AVVIA TENTATIVO
    // =============================================================
    @PostMapping("/{idTest}/avvia")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<AvviaTestResponse> avviaTentativo(
            @PathVariable Long idTest,
            Authentication auth
    ) {
        Utente utente = utenteRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Candidatura> candidature =
                candidaturaRepository.findByCandidato_IdUtente_IdUtente(utente.getIdUtente());

        if (candidature.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessuna candidatura valida");

        Candidatura candidatura = candidature.get(0);
        Long idCandidatura = candidatura.getIdCandidatura();

        Optional<TentativoTest> nonCompletato =
                tentativoTestRepository.findAll().stream()
                        .filter(t -> Objects.equals(t.getIdTest(), idTest)
                                && Objects.equals(t.getIdCandidatura(), idCandidatura)
                                && t.getCompletatoAt() == null)
                        .findFirst();

        if (nonCompletato.isPresent()) {
            TentativoTest t = nonCompletato.get();
            return ResponseEntity.ok(
                    new AvviaTestResponse(
                            t.getIdTentativo(),
                            t.getIdTest(),
                            t.getIniziatoAt().toString()
                    )
            );
        }

        boolean exists = tentativoTestRepository.findAll().stream()
                .anyMatch(t -> Objects.equals(t.getIdTest(), idTest)
                        && Objects.equals(t.getIdCandidatura(), idCandidatura)
                        && t.getCompletatoAt() != null);

        if (exists)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AvviaTestResponse(null, idTest, null));

        TentativoTest nuovo = new TentativoTest();
        nuovo.setIdTest(idTest);
        nuovo.setIdCandidatura(idCandidatura);
        nuovo.setIniziatoAt(LocalDateTime.now());
        nuovo.setPunteggioTotale(0);

        tentativoTestRepository.save(nuovo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AvviaTestResponse(
                        nuovo.getIdTentativo(),
                        idTest,
                        nuovo.getIniziatoAt().toString()
                ));
    }

    // =============================================================
    //   DOMANDE DEL TEST COLLEGATO A TENTATIVO
    // =============================================================
    @GetMapping("/{idTentativo}/domande")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<GetDomandeResponse> domandePerTentativo(@PathVariable Long idTentativo) {

        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Long idTest = t.getIdTest();

        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream()
                .map(d -> new DomandaDto(
                        d.getIdDomanda(),
                        d.getTesto(),
                        opzioneRepository.findByDomanda_IdDomanda(d.getIdDomanda())
                                .stream()
                                .map(o -> new OpzioneDto(
                                        o.getIdOpzione(),
                                        o.getTestoOpzione(),
                                        o.getIsCorretta()))
                                .toList()))
                .toList();

        return ResponseEntity.ok(
                new GetDomandeResponse(
                        idTentativo,
                        test.getIdTest(),
                        test.getTitolo(),
                        test.getDurataMinuti(),
                        domandaDtos
                )
        );
    }

    // =============================================================
    //   DOMANDE TEST SENZA TENTATIVO
    // =============================================================
    @GetMapping("/{idTest}/domande")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<GetDomandeResponse> getDomandeTest(@PathVariable Long idTest) {

        Test test = testService.getTestById(idTest);
        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream()
                .map(domanda -> {
                    List<OpzioneDto> opzioni = opzioneRepository
                            .findByDomanda_IdDomanda(domanda.getIdDomanda())
                            .stream()
                            .map(o -> new OpzioneDto(o.getIdOpzione(), o.getTestoOpzione(), null))
                            .toList();
                    return new DomandaDto(domanda.getIdDomanda(), domanda.getTesto(), opzioni);
                })
                .toList();

        return ResponseEntity.ok(
                new GetDomandeResponse(
                        null,
                        test.getIdTest(),
                        test.getTitolo(),
                        test.getDurataMinuti(),
                        domandaDtos
                )
        );
    }

    // =====================================================================
    // INVIO RISPOSTE
    // =====================================================================
    @PostMapping("/{idTentativo}/risposte")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<InviaRisposteResponse> inviaRisposte(
            @PathVariable Long idTentativo,
            @RequestBody InviaRisposteRequest req
    ) {

        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Long idTest = t.getIdTest();

        List<Risposta> precedenti = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        if (!precedenti.isEmpty()) rispostaRepository.deleteAll(precedenti);

        int totale = 0;

        for (InviaRisposteRequest.RispostaInput r : req.getRisposte()) {

            Domanda domanda = domandaRepository.findById(r.getIdDomanda())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

            Opzione opzione = (r.getIdOpzione() != null)
                    ? opzioneRepository.findById(r.getIdOpzione()).orElse(null)
                    : null;

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());
            int punti = corretta ? 1 : 0;

            totale += punti;

            rispostaRepository.save(new Risposta(
                    punti,
                    t.getIdTentativo(),
                    domanda.getIdDomanda(),
                    r.getIdOpzione()
            ));
        }

        t.setPunteggioTotale(totale);
        t.setCompletatoAt(LocalDateTime.now());

        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String codiceEsito =
                totale >= test.getPunteggioMin() ? "SUPERATO" : "NON_SUPERATO";

        EsitoTentativo esito = esitoTentativoRepository.findByCodice(codiceEsito).orElse(null);
        t.setIdEsitoTentativo(esito);

        tentativoTestRepository.save(t);

        // ====================================================
        // AGGIORNAMENTO AUTOMATICO STATO CANDIDATURA
        // ====================================================
        Candidatura candidaturaAggiornamento =
                candidaturaRepository.findById(t.getIdCandidatura())
                        .orElseThrow(() -> new RuntimeException("Candidatura non trovata"));

        if ("NON_SUPERATO".equals(codiceEsito)) {
            StatoCandidatura respinta = statoCandidaturaRepository.findByCodice("RESPINTA")
                    .orElseThrow(() -> new RuntimeException("Stato RESPINTA non trovato"));
            candidaturaAggiornamento.setStato(respinta);
        } else {
            StatoCandidatura valutazione = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                    .orElseThrow(() -> new RuntimeException("Stato IN_VALUTAZIONE non trovato"));
            candidaturaAggiornamento.setStato(valutazione);
        }

        candidaturaRepository.save(candidaturaAggiornamento);

        int numeroDomande = domandaRepository.countByTest_IdTest(idTest);
        double percentuale =
                numeroDomande > 0 ? (totale * 100.0) / numeroDomande : 0.0;

        return ResponseEntity.ok(
                new InviaRisposteResponse(
                        idTentativo,
                        totale,
                        codiceEsito,
                        percentuale
                )
        );
    }

    // =============================================================
    //   RISULTATO DEL TEST
    // =============================================================
    @GetMapping("/{idTentativo}/risultato")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<RisultatoTentativoDettaglioDto> risultato(@PathVariable Long idTentativo) {

        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Test test = testRepository.findById(t.getIdTest())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Risposta> risposte = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        int numeroDomande = domandaRepository.countByTest_IdTest(test.getIdTest());

        long corrette = risposte.stream()
                .filter(r -> r.getIdOpzione() != null &&
                        Boolean.TRUE.equals(
                                opzioneRepository.findById(r.getIdOpzione().getIdOpzione()).get().getIsCorretta()
                        ))
                .count();

        long errate = risposte.size() - corrette;
        long nonRisposte = numeroDomande - risposte.size();

        double percentuale =
                numeroDomande > 0 ? (t.getPunteggioTotale() * 100.0) / numeroDomande : 0.0;

        return ResponseEntity.ok(
                new RisultatoTentativoDettaglioDto(
                        t.getIdTentativo(),
                        test.getIdTest(),
                        test.getTitolo(),
                        percentuale,
                        t.getPunteggioTotale(),
                        test.getPunteggioMin(),
                        t.getCodiceEsito(),
                        t.getCompletatoAt(),
                        null,
                        numeroDomande,
                        (int) corrette,
                        (int) errate,
                        (int) nonRisposte
                )
        );
    }
}
