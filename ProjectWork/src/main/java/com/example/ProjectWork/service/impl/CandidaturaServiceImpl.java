package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.candidatura.CandidaturaMiaDto;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.service.CandidaturaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CandidaturaServiceImpl implements CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final CandidatoRepository candidatoRepository;
    private final PosizioneRepository posizioneRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;
    private final TentativoTestRepository tentativoTestRepository;

    public CandidaturaServiceImpl(CandidaturaRepository candidaturaRepository,
                                  CandidatoRepository candidatoRepository,
                                  PosizioneRepository posizioneRepository,
                                  StatoCandidaturaRepository statoCandidaturaRepository,
                                  TentativoTestRepository tentativoTestRepository) {
        this.candidaturaRepository = candidaturaRepository;
        this.candidatoRepository = candidatoRepository;
        this.posizioneRepository = posizioneRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
        this.tentativoTestRepository = tentativoTestRepository;
    }

    @Override
    public List<Candidatura> getAllCandidature() {
        return candidaturaRepository.findAll();
    }

    @Override
    public Candidatura createCandidatura(Long idCandidato, Long idPosizione) {
        // Evita candidature duplicate sulla stessa posizione
        boolean exists = candidaturaRepository
                .existsByCandidato_IdCandidatoAndPosizione_IdPosizione(idCandidato, idPosizione);
        if (exists) {
            throw new IllegalStateException("Esiste già una candidatura per questa posizione");
        }

        Candidato candidato = candidatoRepository.findById(idCandidato)
                .orElseThrow(() -> new IllegalArgumentException("Candidato non trovato"));

        Posizione posizione = posizioneRepository.findById(idPosizione)
                .orElseThrow(() -> new IllegalArgumentException("Posizione non trovata"));

        // Recupera lo stato iniziale dal DB (es. "IN_VALUTAZIONE")
        StatoCandidatura statoIniziale = statoCandidaturaRepository.findByCodice("IN_VALUTAZIONE")
                .orElseThrow(() -> new IllegalArgumentException("Stato candidatura 'IN_VALUTAZIONE' non trovato"));

        Candidatura candidatura = new Candidatura();
        candidatura.setCandidato(candidato);
        candidatura.setPosizione(posizione);
        candidatura.setStato(statoIniziale);
        candidatura.setCreatedAT(LocalDate.now());

        candidatura = candidaturaRepository.save(candidatura);

        TentativoTest tentativoTest = new TentativoTest();
        tentativoTest.setIdCandidatura(candidatura.getIdCandidatura());
        tentativoTest.setIdTest(posizione.getIdTest());
        tentativoTest.setIniziatoAt(LocalDateTime.now());

        tentativoTestRepository.save(tentativoTest);

        return candidatura;
    }

    @Override
    public List<Candidatura> getCandidatureByUtente(Utente utente) {
        return candidaturaRepository.findByCandidato_IdUtente(utente);
    }

    @Override
    public void deleteCandidatura(Long id) {
        if (!candidaturaRepository.existsById(id)) {
            throw new RuntimeException("Candidatura non trovata con ID: " + id);
        }
        candidaturaRepository.deleteById(id);
    }

    @Override
    public Candidatura getCandidaturaById(Long id) {
        return candidaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovato con ID: " + id));
    }

    @Override
    public List<CandidaturaMiaDto> getCandidatureDettaglioByUtente(Utente utente) {
        List<Candidatura> candidature = candidaturaRepository.findByCandidato_IdUtente(utente);

        // Carico tutti i tentativi una sola volta e filtro in memoria per semplicità
        List<TentativoTest> tuttiTentativi = tentativoTestRepository.findAll();

        return candidature.stream()
                .map(c -> mappaCandidaturaConPunteggio(c, tuttiTentativi))
                .collect(Collectors.toList());
    }

    private CandidaturaMiaDto mappaCandidaturaConPunteggio(
            Candidatura candidatura,
            List<TentativoTest> tuttiTentativi
    ) {
        // Calcolo punteggio: ultimo tentativo COMPLETATO per questa candidatura
        Integer punteggio = calcolaPunteggioPerCandidatura(candidatura, tuttiTentativi);

        // Posizione
        Posizione posizione = candidatura.getPosizione();
        CandidaturaMiaDto.PosizioneDto posDto = null;
        if (posizione != null) {
            posDto = new CandidaturaMiaDto.PosizioneDto(
                    posizione.getIdPosizione(),
                    posizione.getTitolo(),
                    posizione.getSede(),
                    posizione.getContratto()
            );
        }

        // Stato
        StatoCandidatura stato = candidatura.getStato();
        CandidaturaMiaDto.StatoDto statoDto = null;
        if (stato != null) {
            statoDto = new CandidaturaMiaDto.StatoDto(
                    stato.getCodice(),
                    stato.getDescrizione()
            );
        }

        String createdAtStr = candidatura.getCreatedAT() != null
                ? candidatura.getCreatedAT().toString()
                : null;

        return new CandidaturaMiaDto(
                candidatura.getIdCandidatura(),
                posDto,
                createdAtStr,
                statoDto,
                punteggio
        );
    }

    private Integer calcolaPunteggioPerCandidatura(
            Candidatura candidatura,
            List<TentativoTest> tuttiTentativi
    ) {
        Long idCandidatura = candidatura.getIdCandidatura();

        return tuttiTentativi.stream()
                .filter(t -> t.getIdCandidatura() != null
                        && Objects.equals(
                        t.getIdCandidatura(),
                        idCandidatura
                )
                        && t.getCompletatoAt() != null)
                // prendo l'ultimo tentativo completato in base a completatoAt
                .max(Comparator.comparing(
                        TentativoTest::getCompletatoAt,
                        Comparator.nullsLast(LocalDateTime::compareTo)
                ))
                .map(TentativoTest::getPunteggioTotale)
                .orElse(null);
    }
}
