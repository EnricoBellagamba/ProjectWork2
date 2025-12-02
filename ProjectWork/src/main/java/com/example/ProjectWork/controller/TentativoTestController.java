package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.test.*;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.service.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
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
    private final UtenteRepository utenteRepository;
    private final CandidatoRepository candidatoRepository;

    public TentativoTestController(
            TestService testService,
            DomandaRepository domandaRepository,
            OpzioneRepository opzioneRepository,
            TentativoTestRepository tentativoTestRepository,
            RispostaRepository rispostaRepository,
            CandidaturaRepository candidaturaRepository,
            UtenteRepository utenteRepository,
            CandidatoRepository candidatoRepository
    ) {
        this.testService = testService;
        this.domandaRepository = domandaRepository;
        this.opzioneRepository = opzioneRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.rispostaRepository = rispostaRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
    }

    // ==================== STORICO TENTATIVI (SOLO MIO UTENTE) ====================
    @GetMapping("/tentativi/miei")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<TentativoListItemDto>> getTentativiMiei(Authentication authentication) {

        // 1) utente loggato
        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // ... logica per recuperare i tentativi ...

        // 2) candidature di QUESTO utente
        List<Candidatura> candidatureUtente = candidaturaRepository.findByCandidato_IdUtente(utente);
        Set<Long> idCandidatureUtente = candidatureUtente.stream()
                .map(Candidatura::getIdCandidatura)
                .collect(Collectors.toSet());

        if (idCandidatureUtente.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // 3) tentativi legati SOLO a queste candidature, SOLO COMPLETATI
        List<TentativoTest> completati = tentativoTestRepository.findAll().stream()
                .filter(t -> t.getIdCandidatura() != null
                        && idCandidatureUtente.contains(t.getIdCandidatura().getIdCandidatura())
                        && t.getCompletatoAt() != null)
                .collect(Collectors.toList());

        // 4) per ogni coppia (test, candidatura) tengo SOLO l'ultimo completato
        Map<String, TentativoTest> ultimoPerCoppia = new HashMap<>();
        for (TentativoTest t : completati) {
            Long testId = (t.getIdTest() != null) ? t.getIdTest().getIdTest() : null;
            Long candId = (t.getIdCandidatura() != null) ? t.getIdCandidatura().getIdCandidatura() : null;
            String key = String.valueOf(testId) + "#" + String.valueOf(candId);

            TentativoTest esistente = ultimoPerCoppia.get(key);
            if (esistente == null) {
                ultimoPerCoppia.put(key, t);
            } else {
                LocalDateTime c1 = esistente.getCompletatoAt();
                LocalDateTime c2 = t.getCompletatoAt();
                if (c1 == null || (c2 != null && c2.isAfter(c1))) {
                    ultimoPerCoppia.put(key, t);
                }
            }
        }

        List<TentativoTest> tentativiFinali = new ArrayList<>(ultimoPerCoppia.values());
        tentativiFinali.sort(Comparator.comparing(
                TentativoTest::getCompletatoAt,
                Comparator.nullsLast(LocalDateTime::compareTo)
        ).reversed());

        List<TentativoListItemDto> risultato = tentativiFinali.stream()
                .map(t -> {
                    Test test = t.getIdTest();
                    return new TentativoListItemDto(
                            t.getIdTentativo(),
                            test != null ? test.getIdTest() : null,
                            test != null ? test.getTitolo() : null,
                            test != null ? test.getDurataMinuti() : null,
                            t.getPunteggioTotale(),
                            t.getIdEsitoTentativo() != null
                                    ? t.getIdEsitoTentativo().getCodice()
                                    : "IN_VALUTAZIONE",
                            t.getCompletatoAt() != null ? t.getCompletatoAt().toString() : null
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(risultato);
    }

    // ==================== AVVIO TENTATIVO (SOLO MIO UTENTE) ====================
    @PostMapping("/{idTest}/tentativi/avvia")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<AvviaTestResponse> avviaTest(
            @PathVariable Long idTest,
            @RequestBody(required = false) AvviaTestRequest request,
            Authentication authentication
    ) {
        if (request == null) {
            request = new AvviaTestRequest();
        }

        Test test = testService.getTestById(idTest);

        // 1) utente loggato
        String email = authentication.getName();
        Utente utente = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utente non trovato"));

        // 2) candidato associato all'utente
        Candidato candidato = candidatoRepository.findByIdUtente(utente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profilo candidato non trovato per questo utente"));

        // 3) candidature di questo candidato
        List<Candidatura> candidatureUtente = candidaturaRepository.findByCandidato_IdUtente(utente);
        if (candidatureUtente.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Non hai candidature attive per poter svolgere il test");
        }

        // per ora usiamo la prima candidatura dell'utente
        Candidatura candidatura = candidatureUtente.get(0);

        // 4) controlliamo SOLO tentativi per questo test + QUESTA candidatura
        //    se esiste un tentativo NON completato, lo riusiamo
        TentativoTest nonCompletato = tentativoTestRepository.findAll().stream()
                .filter(t -> t.getIdTest() != null
                        && t.getIdTest().getIdTest().equals(test.getIdTest())
                        && t.getIdCandidatura() != null
                        && t.getIdCandidatura().getIdCandidatura().equals(candidatura.getIdCandidatura())
                        && t.getCompletatoAt() == null)
                .findFirst()
                .orElse(null);

        if (nonCompletato != null) {
            AvviaTestResponse resp = new AvviaTestResponse(
                    nonCompletato.getIdTentativo(),
                    test.getIdTest(),
                    nonCompletato.getIniziatoAt() != null ? nonCompletato.getIniziatoAt().toString() : null
            );
            return ResponseEntity.ok(resp);
        }

        // 5) Se non ci sono tentativi non completati, controlliamo se NE ESISTE GIÀ UNO (completato)
        //    per questo test + questa candidatura -> NON permettiamo di rifarlo (409)
        boolean esisteGia = tentativoTestRepository.findAll().stream()
                .anyMatch(t -> t.getIdTest() != null
                        && t.getIdTest().getIdTest().equals(test.getIdTest())
                        && t.getIdCandidatura() != null
                        && t.getIdCandidatura().getIdCandidatura().equals(candidatura.getIdCandidatura())
                        && t.getCompletatoAt() != null);

        if (esisteGia) {
            // questo "l'hai già fatto" vale SOLO per QUESTO candidato
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AvviaTestResponse(
                            null,
                            test.getIdTest(),
                            null
                    ));
        }

        // 6) Altrimenti CREO un nuovo tentativo per QUESTO candidato
        TentativoTest tentativo = new TentativoTest();
        tentativo.setIdTest(test);
        tentativo.setIdCandidatura(candidatura);
        tentativo.setIniziatoAt(LocalDateTime.now());
        tentativo.setCompletatoAt(null);
        tentativo.setPunteggioTotale(0);

        TentativoTest salvato = tentativoTestRepository.save(tentativo);

        AvviaTestResponse resp = new AvviaTestResponse(
                salvato.getIdTentativo(),
                test.getIdTest(),
                salvato.getIniziatoAt() != null ? salvato.getIniziatoAt().toString() : null
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // ==================== DOMANDE PER TENTATIVO ====================
    @GetMapping("/tentativi/{idTentativo}/domande")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<GetDomandeResponse> getDomandeTentativo(@PathVariable Long idTentativo) {
        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tentativo non trovato."));
        Test test = tentativo.getIdTest();
        List<Domanda> domande = domandaRepository.findByTest_IdTest(test.getIdTest());

        List<DomandaDto> domandaDtos = domande.stream().map(domanda -> {
            List<Opzione> opzioni = opzioneRepository.findByDomanda_IdDomanda(domanda.getIdDomanda());
            List<OpzioneDto> opzioneDtos = opzioni.stream()
                    .map(o -> new OpzioneDto(o.getIdOpzione(), o.getTestoOpzione(), o.getIsCorretta()))
                    .collect(Collectors.toList());
            return new DomandaDto(domanda.getIdDomanda(), domanda.getTesto(), opzioneDtos);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new GetDomandeResponse(
                tentativo.getIdTentativo(),
                test.getIdTest(),
                test.getTitolo(),
                test.getDurataMinuti(),
                domandaDtos
        ));
    }

    // ==================== INVIO RISPOSTE ====================
    @PostMapping("/tentativi/{idTentativo}/risposte")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<InviaRisposteResponse> inviaRisposte(
            @PathVariable Long idTentativo,
            @RequestBody InviaRisposteRequest request
    ) {
        // 1. Validazione input base
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request vuota");
        }
        // Se il tuo DTO ha questo setter, usalo, altrimenti è superfluo se usi l'ID nel path
        request.setIdTentativo(idTentativo);

        // 2. Recupera Tentativo e Test
        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tentativo non trovato."));

        Test test = tentativo.getIdTest();

        // 3. Reset: cancella risposte precedenti se l'utente sta ritentando lo stesso tentativo (opzionale, ma pulito)
        List<Risposta> rispostePrecedenti = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        if (!rispostePrecedenti.isEmpty()) {
            rispostaRepository.deleteAll(rispostePrecedenti);
        }

        int punteggioTotale = 0;

        // VALORE FISSO DELLA DOMANDA
        // Poiché Domanda non ha un campo punteggio, usiamo un valore standard.
        // MODIFICA '1' SE VUOI ASSEGNARE PIÙ PUNTI A DOMANDA.
        final int PUNTI_PER_DOMANDA = 1;

        // 4. Ciclo sulle risposte inviate
        if (request.getRisposte() != null) {
            for (InviaRisposteRequest.RispostaInput input : request.getRisposte()) {

                // A. Trova la domanda
                Domanda domanda = domandaRepository.findById(input.getIdDomanda())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Domanda non trovata id: " + input.getIdDomanda()));

                // B. Trova l'opzione scelta (gestisce il caso null se l'utente non ha risposto)
                Opzione opzione = input.getIdOpzione() != null
                        ? opzioneRepository.findById(input.getIdOpzione()).orElse(null)
                        : null;

                // C. Verifica se è corretta
                boolean isCorretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());

                // D. Calcola i punti assegnati (Punti pieni se corretta, 0 se errata/null)
                // Questa era la riga che causava l'errore di compilazione
                int punteggioAssegnato = isCorretta ? PUNTI_PER_DOMANDA : 0;

                // E. Aggiorna il totale
                punteggioTotale += punteggioAssegnato;

                // F. Salva la Risposta nel DB
                Risposta risposta = new Risposta(punteggioAssegnato, tentativo, domanda, opzione);
                rispostaRepository.save(risposta);
            }
        }

        // 5. Aggiorna il Tentativo
        tentativo.setPunteggioTotale(punteggioTotale);
        tentativo.setCompletatoAt(LocalDateTime.now());

        // Calcola esito
        String esito = punteggioTotale >= test.getPunteggioMin() ? "SUPERATO" : "NON_SUPERATO";
        // es: tentativo.setEsito(esito);

        tentativoTestRepository.save(tentativo);

        // 6. Costruisci la risposta per il client
        InviaRisposteResponse response = new InviaRisposteResponse(
                tentativo.getIdTentativo(),
                punteggioTotale,
                esito
        );

        return ResponseEntity.ok(response);
    }

    // ==================== RISULTATO TENTATIVO ====================
    @GetMapping("/tentativi/{idTentativo}/risultati")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<RisultatoTentativoDettaglioDto> getRisultatoTentativo(@PathVariable Long idTentativo) {
        TentativoTest tentativo = tentativoTestRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tentativo non trovato."));
        Test test = tentativo.getIdTest();

        List<Risposta> risposte = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);
        int numeroDomande = domandaRepository.findByTest_IdTest(test.getIdTest()).size();

        // Calcolo delle risposte (corrette/errate) basato sulla presenza e correttezza dell'opzione scelta
        int corrette = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null && Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();
        int errate = (int) risposte.stream()
                .filter(r -> r.getIdOpzione() != null && !Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();
        int nonRisposte = Math.max(0, numeroDomande - (corrette + errate));

        String esito = tentativo.getIdEsitoTentativo() == null
                ? "IN_VALUTAZIONE"
                : tentativo.getIdEsitoTentativo().getCodice();

        RisultatoTentativoDettaglioDto dto = new RisultatoTentativoDettaglioDto(
                tentativo.getIdTentativo(),
                test.getIdTest(),
                test.getTitolo(),
                tentativo.getPunteggioTotale(),
                test.getPunteggioMin(),
                esito,
                tentativo.getCompletatoAt() != null ? tentativo.getCompletatoAt().toString() : null,
                null,
                numeroDomande,
                corrette,
                errate,
                nonRisposte
        );

        return ResponseEntity.ok(dto);
    }

    // ==================== STRUTTURA COMPLETA TEST (PER HR) ====================
    @GetMapping("/{idTest}/struttura-tentativo/{idTentativo}")
    public ResponseEntity<StrutturaTestResponse> getStrutturaPerTentativo(
            @PathVariable Long idTest,
            @PathVariable Long idTentativo
    ) {
        Test test = testService.getTestById(idTest);
        List<Domanda> domande = domandaRepository.findByTest_IdTest(idTest);

        List<DomandaDto> domandaDtos = domande.stream()
                .map(domanda -> {
                    List<Opzione> opzioni = opzioneRepository.findByDomanda_IdDomanda(domanda.getIdDomanda());
                    List<OpzioneDto> opzioneDtos = opzioni.stream()
                            .map(o -> new OpzioneDto(o.getIdOpzione(), o.getTestoOpzione(), o.getIsCorretta()))
                            .collect(Collectors.toList());
                    return new DomandaDto(domanda.getIdDomanda(), domanda.getTesto(), opzioneDtos);
                })
                .collect(Collectors.toList());

        int numeroDomande = domandaDtos.size();
        Integer punteggioMin = test.getPunteggioMin();
        String tipo = test.getTipoTest() != null ? test.getTipoTest().getCodice() : null; // Assumendo che 'tipo' sia un campo Codice nell'Entity TipoTest

        StrutturaTestResponse resp = new StrutturaTestResponse(
                test.getIdTest(),
                test.getTitolo(),
                test.getDescrizione(),
                test.getDurataMinuti(),
                numeroDomande,
                punteggioMin,
                tipo,
                domandaDtos
        );

        return ResponseEntity.ok(resp);
    }
}