package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.test.*;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
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
@RequestMapping("/api/test/tentativi")
public class TentativoTestController {

    private final DomandaRepository domandaRepository;
    private final OpzioneRepository opzioneRepository;
    private final TentativoTestRepository tentativoRepository;
    private final RispostaRepository rispostaRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final UtenteRepository utenteRepository;
    private final CandidatoRepository candidatoRepository;
    private final TestRepository testRepository;
    private final EsitoTentativoRepository esitoRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;

    public TentativoTestController(
            DomandaRepository domandaRepository,
            OpzioneRepository opzioneRepository,
            TentativoTestRepository tentativoRepository,
            RispostaRepository rispostaRepository,
            CandidaturaRepository candidaturaRepository,
            UtenteRepository utenteRepository,
            CandidatoRepository candidatoRepository,
            TestRepository testRepository,
            EsitoTentativoRepository esitoRepository,
            StatoCandidaturaRepository statoCandidaturaRepository
    ) {
        this.domandaRepository = domandaRepository;
        this.opzioneRepository = opzioneRepository;
        this.tentativoRepository = tentativoRepository;
        this.rispostaRepository = rispostaRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.utenteRepository = utenteRepository;
        this.candidatoRepository = candidatoRepository;
        this.testRepository = testRepository;
        this.esitoRepository = esitoRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
    }

    // =============================================================
    //   STORICO TENTATIVI COMPLETATI
    // =============================================================
    @GetMapping("/miei")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<List<TentativoListItemDto>> mieiTentativi(Authentication auth) {
        Utente utente = utenteRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Candidatura> candidature = candidaturaRepository.findByCandidato_IdUtente(utente);
        if (candidature.isEmpty())
            return ResponseEntity.ok(List.of());

        Set<Long> idCandidature = candidature.stream()
                .map(Candidatura::getIdCandidatura)
                .collect(Collectors.toSet());

        List<TentativoTest> completati = tentativoRepository.findAll().stream()
                .filter(t -> idCandidature.contains(t.getIdCandidatura()) && t.getCompletatoAt() != null)
                .toList();

        List<TentativoListItemDto> result = completati.stream()
                .sorted(Comparator.comparing(TentativoTest::getCompletatoAt).reversed())
                .map(t -> {
                    Test test = testRepository.findById(t.getIdTest()).orElse(null);
                    return new TentativoListItemDto(
                            t.getIdTentativo(),
                            test != null ? test.getIdTest() : null,
                            test != null ? test.getTitolo() : "",
                            test != null ? test.getDurataMinuti() : null,
                            t.getPunteggioTotale(),
                            t.getCodiceEsito(),
                            t.getCompletatoAt() != null ? t.getCompletatoAt().toString() : null
                    );
                })
                .toList();

        return ResponseEntity.ok(result);
    }


    // =============================================================
    //   AVVIARE TENTATIVO
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

        Test test = testRepository.findById(idTest)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Candidatura> candidature = candidaturaRepository.findByCandidato_IdUtente(utente);
        if (candidature.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nessuna candidatura valida");

        Candidatura candidatura = candidature.get(0);

        Optional<TentativoTest> aperto = tentativoRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getIdTest(), idTest)
                        && Objects.equals(t.getIdCandidatura(), candidatura.getIdCandidatura())
                        && t.getCompletatoAt() == null)
                .findFirst();

        if (aperto.isPresent()) {
            TentativoTest t = aperto.get();
            return ResponseEntity.ok(
                    new AvviaTestResponse(t.getIdTentativo(), idTest, t.getIniziatoAt().toString())
            );
        }

        boolean esisteCompletato = tentativoRepository.findAll().stream()
                .anyMatch(t -> Objects.equals(t.getIdTest(), idTest)
                        && Objects.equals(t.getIdCandidatura(), candidatura.getIdCandidatura())
                        && t.getCompletatoAt() != null);

        if (esisteCompletato)
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AvviaTestResponse(null, idTest, null));

        TentativoTest nuovo = new TentativoTest();
        nuovo.setIdTest(idTest);
        nuovo.setIdCandidatura(candidatura.getIdCandidatura());
        nuovo.setIniziatoAt(LocalDateTime.now());
        nuovo.setCompletatoAt(null);
        nuovo.setPunteggioTotale(0);

        tentativoRepository.save(nuovo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AvviaTestResponse(nuovo.getIdTentativo(), idTest, nuovo.getIniziatoAt().toString()));
    }


    // =============================================================
    //   DOMANDE DEL TEST
    // =============================================================
    @GetMapping("/{idTentativo}/domande")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<GetDomandeResponse> domande(@PathVariable Long idTentativo) {

        TentativoTest t = tentativoRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Test test = testRepository.findById(t.getIdTest())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Domanda> domande = domandaRepository.findByTest_IdTest(test.getIdTest());

        return ResponseEntity.ok(
                new GetDomandeResponse(
                        idTentativo,
                        test.getIdTest(),
                        test.getTitolo(),
                        test.getDurataMinuti(),
                        domande.stream()
                                .map(d -> new DomandaDto(
                                        d.getIdDomanda(),
                                        d.getTesto(),
                                        opzioneRepository.findByDomanda_IdDomanda(d.getIdDomanda())
                                                .stream()
                                                .map(o -> new OpzioneDto(o.getIdOpzione(), o.getTestoOpzione(), o.getIsCorretta()))
                                                .toList()
                                )).toList()
                )
        );
    }


    // =============================================================
    //   INVIA RISPOSTE
    // =============================================================
    @PostMapping("/{idTentativo}/risposte")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<InviaRisposteResponse> inviaRisposte(
            @PathVariable Long idTentativo,
            @RequestBody InviaRisposteRequest req
    ) {

        TentativoTest t = tentativoRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (t.getCompletatoAt() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tentativo già concluso");

        Test test = testRepository.findById(t.getIdTest())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        LocalDateTime deadline = t.getIniziatoAt().plusMinutes(test.getDurataMinuti());
        boolean scaduto = LocalDateTime.now().isAfter(deadline);

        int totale = 0;

        for (InviaRisposteRequest.RispostaInput r : req.getRisposte()) {

            Domanda domanda = domandaRepository.findById(r.getIdDomanda())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Domanda non valida"));

            Opzione opzione = null;
            if (r.getIdOpzione() != null) {
                opzione = opzioneRepository.findById(r.getIdOpzione())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opzione non valida"));

                if (!Objects.equals(opzione.getDomanda().getIdDomanda(), domanda.getIdDomanda()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Opzione non appartenente alla domanda");
            }

            boolean corretta = opzione != null && Boolean.TRUE.equals(opzione.getIsCorretta());
            int punti = corretta ? 1 : 0;
            totale += punti;

            Optional<Risposta> esistente = rispostaRepository.findByIdTentativoAndIdDomanda(t, domanda);

            Risposta rs = esistente.orElseGet(Risposta::new);
            rs.setIdTentativo(t);
            rs.setIdDomanda(domanda);
            rs.setIdOpzione(opzione);
            rs.setPunteggioAssegnato(punti);

            rispostaRepository.save(rs);
        }

        t.setPunteggioTotale(totale);

        EsitoTentativo esito;
        if (totale < test.getPunteggioMin()) {
            esito = esitoRepository.findByCodice("NON_SUPERATO")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } else {
            esito = esitoRepository.findByCodice("IN_VALUTAZIONE")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }

        t.setIdEsitoTentativo(esito);
        t.setCodiceEsito(esito.getCodice());
        t.setCompletatoAt(LocalDateTime.now());
        tentativoRepository.save(t);

        Candidatura candidatura = candidaturaRepository.findById(t.getIdCandidatura())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (esito.getCodice().equals("NON_SUPERATO")) {
            StatoCandidatura respinta = statoCandidaturaRepository.findByCodice("RESPINTA")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            candidatura.setStato(respinta);
        } else {
            StatoCandidatura inVal = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            candidatura.setStato(inVal);
        }

        candidaturaRepository.save(candidatura);

        return ResponseEntity.ok(
                new InviaRisposteResponse(idTentativo, totale, esito.getCodice())
        );
    }


    // =============================================================
    //   RISULTATO DEL TENTATIVO
    // =============================================================
    @GetMapping("/{idTentativo}/risultato")
    @PreAuthorize("hasRole('CANDIDATO')")
    public ResponseEntity<RisultatoTentativoDettaglioDto> risultato(@PathVariable Long idTentativo) {

        TentativoTest t = tentativoRepository.findById(idTentativo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Test test = testRepository.findById(t.getIdTest())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<Domanda> domande = domandaRepository.findByTest_IdTest(test.getIdTest());
        List<Risposta> risposte = rispostaRepository.findByIdTentativo_IdTentativo(idTentativo);

        long corrette = risposte.stream()
                .filter(r -> r.getIdOpzione() != null && Boolean.TRUE.equals(r.getIdOpzione().getIsCorretta()))
                .count();

        long errate = risposte.size() - corrette;
        long nonRisposte = domande.size() - risposte.size();

        return ResponseEntity.ok(
                new RisultatoTentativoDettaglioDto(
                        idTentativo,
                        test.getIdTest(),
                        test.getTitolo(),
                        t.getPunteggioTotale(),
                        test.getPunteggioMin(),
                        t.getCodiceEsito(),
                        t.getCompletatoAt().toString(),
                        null,
                        domande.size(),
                        (int) corrette,
                        (int) errate,
                        (int) nonRisposte
                )
        );
    }
}
