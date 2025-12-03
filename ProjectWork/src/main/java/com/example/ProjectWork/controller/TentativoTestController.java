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
import com.example.ProjectWork.dto.test.CompletaTestRequest;


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
            TestRepository testRepository, PosizioneRepository posizioneRepository, StatoCandidaturaRepository statoCandidaturaRepository, EsitoTentativoRepository esitoTentativoRepository
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

    // TentativoTestController.java

    @PostMapping("/completa")
    @PreAuthorize("hasRole('CANDIDATO')")
    @Transactional
    public ResponseEntity<InviaRisposteResponse> completaTestECandidatura(
            @RequestBody CompletaTestRequest request,
            Authentication authentication
    ) {
        if (request == null) {
            throw new RuntimeException("Request vuota");
        }

        // 1) Recupera utente loggato
        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // 2) Recupera o crea il profilo candidato
        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseGet(() -> {
                    Candidato nuovo = new Candidato();
                    nuovo.setIdUtente(utente);
                    nuovo.setActive(true);
                    return candidatoRepository.save(nuovo);
                });

        // 3) Recupera test e posizione
        Test test = testService.getTestById(request.getIdTest());
        Posizione posizione = posizioneRepository.findById(request.getIdPosizione())
                .orElseThrow(() -> new RuntimeException("Posizione non trovata"));

        // 4) Verifica che non esista già una candidatura per questa posizione
        boolean esisteCandidatura = candidaturaRepository
                .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(
                        candidato.getIdCandidato(),
                        request.getIdPosizione()
                );

        if (esisteCandidatura) {
            throw new RuntimeException("Hai già una candidatura attiva per questa posizione");
        }

        // ✅ 5) CREA LA CANDIDATURA
        StatoCandidatura statoIniziale = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                .orElseThrow(() -> new RuntimeException("Stato 'IN_VALUTAZIONE' non trovato"));

        Candidatura candidatura = new Candidatura();
        candidatura.setCandidato(candidato);
        candidatura.setPosizione(posizione);
        candidatura.setStato(statoIniziale);
        candidatura.setCreatedAT(LocalDate.now());

        candidatura = candidaturaRepository.save(candidatura);
        Long idCandidatura = candidatura.getIdCandidatura();

        // ✅ 6) CREA IL TENTATIVO TEST
        TentativoTest tentativo = new TentativoTest();
        tentativo.setIdCandidatura(idCandidatura);
        tentativo.setIdTest(test.getIdTest());
        tentativo.setIniziatoAt(request.getIniziatoAt());
        tentativo.setCompletatoAt(LocalDateTime.now());

        TentativoTest tentativoSalvato = tentativoTestRepository.save(tentativo);

        // ✅ 7) CALCOLA PUNTEGGIO con punteggio FISSO = 1 per risposta corretta
        List<Domanda> domandeTest = domandaRepository.findByTest_IdTest(test.getIdTest());
        int punteggioTotale = 0;
        final int PUNTI_PER_DOMANDA = 1; // ✅ Punteggio fisso

        for (CompletaTestRequest.RispostaInput input : request.getRisposte()) {
            Domanda domanda = domandaRepository.findById(input.getIdDomanda())
                    .orElseThrow(() -> new RuntimeException("Domanda non trovata"));

            Opzione opzione = input.getIdOpzione() != null
                    ? opzioneRepository.findById(input.getIdOpzione()).orElse(null)
                    : null;

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());
            if (corretta) {
                punteggioTotale += PUNTI_PER_DOMANDA; // ✅ Usa punteggio fisso
            }

            Risposta risposta = new Risposta(PUNTI_PER_DOMANDA, tentativoSalvato, domanda, opzione);
            rispostaRepository.save(risposta);
        }

        // ✅ 8) AGGIORNA PUNTEGGIO ED ESITO
        tentativoSalvato.setPunteggioTotale(punteggioTotale);

        // ✅ Calcola esito in base al punteggio minimo del test
        String codiceEsito = punteggioTotale >= test.getPunteggioMin() ? "SUPERATO" : "NON_SUPERATO";
        EsitoTentativo esito = esitoTentativoRepository.findByCodice(codiceEsito)
                .orElse(null);
        tentativoSalvato.setIdEsitoTentativo(esito);

        tentativoTestRepository.save(tentativoSalvato);

        // ✅ 9) RISPOSTA
        InviaRisposteResponse response = new InviaRisposteResponse(
                tentativoSalvato.getIdTentativo(),
                punteggioTotale,
                codiceEsito
        );

        return ResponseEntity.ok(response);
    }




    // =============================================================
    //   STORICO TENTATIVI DELL’UTENTE LOGGATO
    // =============================================================
    @GetMapping("/miei")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<TentativoListItemDto>> getMieiTentativi(Authentication auth) {

        Utente utente = utenteRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // Recupero candidature dell’utente
        List<Candidatura> candidature = candidaturaRepository.findByCandidato_IdUtente(utente);

        if (candidature.isEmpty())
            return ResponseEntity.ok(List.of());

        Set<Long> idCandidature = candidature.stream()
                .map(Candidatura::getIdCandidatura)
                .collect(Collectors.toSet());

        // Prendo SOLO tentativi COMPLETATI dell’utente
        List<TentativoTest> completati = tentativoTestRepository.findAll().stream()
                .filter(t -> t.getIdCandidatura() != null &&
                        idCandidature.contains(t.getIdCandidatura()) &&
                        t.getCompletatoAt() != null)
                .toList();

        // Mantengo solo l’ultimo per coppia (testId, candidaturaId)
        Map<String, TentativoTest> ultimo = new HashMap<>();

        for (TentativoTest t : completati) {
            String key = t.getIdTest() + "#" + t.getIdCandidatura();
            TentativoTest existing = ultimo.get(key);
            if (existing == null ||
                    (t.getCompletatoAt() != null &&
                            t.getCompletatoAt().isAfter(existing.getCompletatoAt()))) {
                ultimo.put(key, t);
            }
        }

        List<TentativoListItemDto> result = ultimo.values().stream()
                .sorted(Comparator.comparing(
                        TentativoTest::getCompletatoAt,
                        Comparator.nullsLast(LocalDateTime::compareTo)
                ).reversed())
                .map(t -> new TentativoListItemDto(
                        t.getIdTentativo(),
                        t.getPunteggioTotale(),
                        t.getCodiceEsito(),
                        t.getCompletatoAt()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }


    // =============================================================
    //   AVVIA TENTATIVO PER UN TEST
    // =============================================================
    @PostMapping("/{idTest}/avvia")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<AvviaTestResponse> avviaTentativo(
            @PathVariable Long idTest,
            Authentication auth
    ) {
        Utente utente = utenteRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profilo candidato non trovato"));

        // 1) Candidatura associata
        List<Candidatura> candidature = candidaturaRepository.findByCandidato_IdUtente(utente);
        if (candidature.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessuna candidatura valida");

        Candidatura candidatura = candidature.get(0);
        Long idCandidatura = candidatura.getIdCandidatura();

        // 2) Tentativo NON COMPLETATO → riusalo
        Optional<TentativoTest> nonCompletato = tentativoTestRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getIdTest(), idTest)
                        && Objects.equals(t.getIdCandidatura(), idCandidatura)
                        && t.getCompletatoAt() == null)
                .findFirst();

        if (nonCompletato.isPresent()) {
            TentativoTest t = nonCompletato.get();
            return ResponseEntity.ok(
                    new AvviaTestResponse(t.getIdTentativo(), t.getIdTest(), t.getIniziatoAt().toString())
            );
        }

        // 3) Tentativo GIÀ COMPLETATO → non può rifarlo
        boolean esisteCompletato = tentativoTestRepository.findAll().stream()
                .anyMatch(t -> Objects.equals(t.getIdTest(), idTest)
                        && Objects.equals(t.getIdCandidatura(), idCandidatura)
                        && t.getCompletatoAt() != null);

        if (esisteCompletato)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AvviaTestResponse(null, idTest, null));

        // 4) Creo NUOVO tentativo
        TentativoTest nuovo = new TentativoTest();
        nuovo.setIdTest(idTest);
        nuovo.setIdCandidatura(idCandidatura);
        nuovo.setIniziatoAt(LocalDateTime.now());
        nuovo.setCompletatoAt(null);
        nuovo.setPunteggioTotale(0);

        tentativoTestRepository.save(nuovo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AvviaTestResponse(nuovo.getIdTentativo(), idTest, nuovo.getIniziatoAt().toString()));
    }


    // =============================================================
    //   DOMANDE DEL TEST ASSOCIATO AL TENTATIVO
    // =============================================================
    @GetMapping("/{idTentativo}/domande")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<GetDomandeResponse> domandePerTentativo(@PathVariable Long idTentativo) {

        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tentativo non trovato"));

        Long idTest = t.getIdTest();

        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test non trovato"));

        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream()
                .map(d -> new DomandaDto(
                        d.getIdDomanda(),
                        d.getTesto(),
                        opzioneRepository.findByDomanda_IdDomanda(d.getIdDomanda())
                                .stream()
                                .map(o -> new OpzioneDto(o.getIdOpzione(), o.getTestoOpzione(), o.getIsCorretta()))
                                .toList()
                ))
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


    @GetMapping("/{idTest}/domande")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<GetDomandeResponse> getDomandeTest(@PathVariable Long idTest) {
        Test test = testService.getTestById(idTest);
        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream().map(domanda -> {
            List<Opzione> opzioni = opzioneRepository.findByDomanda_IdDomanda(domanda.getIdDomanda());
            List<OpzioneDto> opzioneDtos = opzioni.stream()
                    .map(o -> new OpzioneDto(o.getIdOpzione(), o.getTestoOpzione(), null)) // ✅ NON esporre isCorretta
                    .collect(Collectors.toList());
            return new DomandaDto(domanda.getIdDomanda(), domanda.getTesto(), opzioneDtos);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new GetDomandeResponse(
                null, // Nessun idTentativo
                test.getIdTest(),
                test.getTitolo(),
                test.getDurataMinuti(),
                domandaDtos
        ));
    }

    // =============================================================
    //   INVIO RISPOSTE
    // =============================================================
    @PostMapping("/{idTentativo}/risposte")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<InviaRisposteResponse> inviaRisposte(
            @PathVariable Long idTentativo,
            @RequestBody InviaRisposteRequest req
    ) {

        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tentativo non trovato"));

        Long idTest = t.getIdTest();

        List<Risposta> precedenti = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        if (!precedenti.isEmpty()) rispostaRepository.deleteAll(precedenti);

        int totale = 0;

        for (InviaRisposteRequest.RispostaInput r : req.getRisposte()) {

            Domanda domanda = domandaRepository.findById(r.getIdDomanda())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Domanda non valida"));

            Opzione opzione = (r.getIdOpzione() != null)
                    ? opzioneRepository.findById(r.getIdOpzione()).orElse(null)
                    : null;

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());

            int punti = corretta ? 1 : 0;
            totale += punti;


            Risposta rs = new Risposta(punti, t.getIdTentativo(), domanda.getIdDomanda(), r.getIdOpzione());
            rispostaRepository.save(rs);
        }

        t.setPunteggioTotale(totale);
        t.setCompletatoAt(LocalDateTime.now());
        t.setCodiceEsito(null); // popolalo se hai una tabella esito

        tentativoTestRepository.save(t);

        return ResponseEntity.ok(
                new InviaRisposteResponse(idTentativo, totale, totale >= testRepository.findById(idTest).get().getPunteggioMin() ? "SUPERATO" : "NON_SUPERATO")
        );
    }


    // =============================================================
    //   RISULTATO DEL TENTATIVO
    // =============================================================
    @GetMapping("/{idTentativo}/risultato")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<RisultatoTentativoDettaglioDto> risultato(@PathVariable Long idTentativo) {

        TentativoTest t = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tentativo non trovato"));

        Test test = testRepository.findById(t.getIdTest())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test non trovato"));

        List<Risposta> risposte = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        int numeroDomande = domandaRepository.findByTest_IdTest(test.getIdTest()).size();

        long corrette = risposte.stream().filter(r -> r.getIdOpzione() != null &&
                Boolean.TRUE.equals(
                        opzioneRepository.findById(r.getIdOpzione().getIdOpzione()).get().getIsCorretta()
                )).count();

        long errate = risposte.size() - corrette;
        long nonRisposte = numeroDomande - risposte.size();

        return ResponseEntity.ok(
                new RisultatoTentativoDettaglioDto(
                        idTentativo,
                        test.getIdTest(),
                        test.getTitolo(),
                        t.getPunteggioTotale(),
                        test.getPunteggioMin(),
                        t.getCodiceEsito(),
                        t.getCompletatoAt() != null ? t.getCompletatoAt().toLocalDate() : null,
                        null,
                        numeroDomande,
                        (int) corrette,
                        (int) errate,
                        (int) nonRisposte
                )
        );
    }
}
