package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.service.CandidaturaService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidaturaServiceImpl implements CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final CandidatoRepository candidatoRepository;
    private final PosizioneRepository posizioneRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;
    private final TentativoTestRepository tentativoTestRepository;
    private final TestRepository testRepository;

    public CandidaturaServiceImpl(
            CandidaturaRepository candidaturaRepository,
            CandidatoRepository candidatoRepository,
            PosizioneRepository posizioneRepository,
            StatoCandidaturaRepository statoCandidaturaRepository,
            TentativoTestRepository tentativoTestRepository,
            TestRepository testRepository
    ) {
        this.candidaturaRepository = candidaturaRepository;
        this.candidatoRepository = candidatoRepository;
        this.posizioneRepository = posizioneRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.testRepository = testRepository;
    }

    // ================================================================
    // GET ALL
    // ================================================================
    @Override
    public List<Candidatura> getAllCandidature() {
        return candidaturaRepository.findAll();
    }

    // ================================================================
    // GET BY ID
    // ================================================================
    @Override
    public Candidatura getCandidaturaById(Long id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // ================================================================
    // CREA CANDIDATURA (solo quando richiesto dal sistema, non dal controller del test)
    // ================================================================
    @Override
    public Candidatura createCandidatura(Long idCandidato, Long idPosizione) {

        boolean exists = candidaturaRepository
                .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(idCandidato, idPosizione);

        if (exists) {
            throw new IllegalStateException("Esiste già una candidatura per questa posizione");
        }

        Candidato candidato = candidatoRepository.findById(idCandidato)
                .orElseThrow(() -> new IllegalArgumentException("Candidato non trovato"));

        Posizione posizione = posizioneRepository.findById(idPosizione)
                .orElseThrow(() -> new IllegalArgumentException("Posizione non trovata"));

        StatoCandidatura statoIniziale = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                .orElseThrow(() -> new IllegalStateException("Stato 'IN_VALUTAZIONE' non trovato"));

        Candidatura candidatura = new Candidatura();
        candidatura.setCandidato(candidato);
        candidatura.setPosizione(posizione);
        candidatura.setStato(statoIniziale);
        candidatura.setCreatedAt(LocalDate.now());

        candidatura = candidaturaRepository.save(candidatura);

        // Se la posizione ha un test → crea il tentativo base
        if (posizione.getIdTest() != null) {

            TentativoTest t = new TentativoTest();
            t.setIdCandidatura(candidatura.getIdCandidatura());
            t.setIdTest(posizione.getIdTest());
            t.setPunteggioTotale(0);
            t.setIniziatoAt(LocalDateTime.now());
            t.setCompletatoAt(null);

            tentativoTestRepository.save(t);
        }

        return candidatura;
    }

    // ================================================================
    // DELETE
    // ================================================================
    @Override
    public void deleteCandidatura(Long id) {
        if (!candidaturaRepository.existsById(id)) {
            throw new RuntimeException("Candidatura non trovata");
        }
        candidaturaRepository.deleteById(id);
    }

    // ================================================================
    // GET CANDIDATURE (ruolo candidato)
    // ================================================================
    @Override
    public List<Candidatura> getCandidatureByUtente(Utente utente) {
        return candidaturaRepository.findByCandidato_IdUtente_IdUtente(utente.getIdUtente());
    }

    // ================================================================
    // GET DETTAGLIO CANDIDATURE CANDIDATO
    // ================================================================
    @Override
    public List<CandidaturaMiaDto> getCandidatureDettaglioByUtente(Utente utente) {

        List<Candidatura> candidature =
                candidaturaRepository.findByCandidato_IdUtente_IdUtente(utente.getIdUtente());

        return candidature.stream()
                .map(c -> {

                    // Ultimo tentativo completato
                    Integer punteggio = tentativoTestRepository
                            .findAllByIdCandidatura(c.getIdCandidatura())
                            .stream()
                            .filter(t -> t.getCompletatoAt() != null)
                            .max(Comparator.comparing(TentativoTest::getCompletatoAt))
                            .map(TentativoTest::getPunteggioTotale)
                            .orElse(null);

                    // Numero domande del test collegato alla posizione
                    Integer numeroDomande = null;
                    Long idTest = c.getPosizione().getIdTest();

                    if (idTest != null) {
                        Test test = testRepository.findById(idTest).orElse(null);
                        if (test != null) {
                            numeroDomande = test.getNumeroDomande();
                        }
                    }

                    // DTO posizione interna
                    Posizione p = c.getPosizione();
                    CandidaturaMiaDto.PosizioneDto posDto =
                            new CandidaturaMiaDto.PosizioneDto(
                                    p.getIdPosizione(),
                                    p.getTitolo(),
                                    p.getSede(),
                                    p.getContratto()
                            );

                    StatoCandidatura stato = c.getStato();
                    CandidaturaMiaDto.StatoDto st =
                            new CandidaturaMiaDto.StatoDto(stato.getCodice(), stato.getDescrizione());

                    return new CandidaturaMiaDto(
                            c.getIdCandidatura(),
                            posDto,
                            c.getCreatedAt() != null ? c.getCreatedAt().toString() : null,
                            st,
                            punteggio,
                            numeroDomande
                    );
                })
                .collect(Collectors.toList());
    }

    // ================================================================
    // HR — AGGIORNA STATO CANDIDATURA
    // ================================================================
    @Override
    public Candidatura aggiornaStato(Long idCandidatura, StatoCandidatura nuovoStato) {

        Candidatura c = candidaturaRepository.findById(idCandidatura)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidatura non trovata"));

        c.setStato(nuovoStato);

        return candidaturaRepository.save(c);
    }
}
