package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.candidatura.CandidatoPerPosizioneDTO;
import com.example.ProjectWork.dto.candidatura.Top5Request;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PosizioneServiceImpl implements PosizioneService {

    private final PosizioneRepository posizioneRepository;
    private final SettoreRepository settoreRepository;
    private final StatoPosizioneRepository statoPosizioneRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final TentativoTestRepository tentativoTestRepository;
    private final StatoCandidaturaRepository statoCandidaturaRepository;
    private final TestRepository testRepository;


    public PosizioneServiceImpl(
            PosizioneRepository posizioneRepository,
            SettoreRepository settoreRepository,
            StatoPosizioneRepository statoPosizioneRepository,
            CandidaturaRepository candidaturaRepository,
            TentativoTestRepository tentativoTestRepository,
            StatoCandidaturaRepository statoCandidaturaRepository, TestRepository testRepository
    ) {
        this.posizioneRepository = posizioneRepository;
        this.settoreRepository = settoreRepository;
        this.statoPosizioneRepository = statoPosizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
        this.testRepository = testRepository;
    }

    // ============================================================
    // CRUD POSIZIONI
    // ============================================================

    @Override
    public List<Posizione> getAllPosizioni() {
        return posizioneRepository.findAll();
    }

    @Override
    public Posizione createPosizione(Posizione posizione) {

        if (posizione.getCandidatureRicevute() == null) {
            posizione.setCandidatureRicevute(0L);
        }

        if (posizione.getIdSettore() == null) {
            posizione.setIdSettore(
                    settoreRepository.findById(1L)
                            .orElseThrow(() -> new RuntimeException("Settore default non trovato"))
            );
        }

        if (posizione.getIdStatoPosizione() == null) {
            StatoPosizione aperta = statoPosizioneRepository.findByCodice("APERTA")
                    .orElseThrow(() -> new RuntimeException("Stato posizione 'APERTA' non trovato"));
            posizione.setIdStatoPosizione(aperta);
        }

        return posizioneRepository.save(posizione);
    }

    @Override
    public void deletePosizione(Long id) {
        if (!posizioneRepository.existsById(id)) {
            throw new RuntimeException("Posizione non trovata");
        }
        posizioneRepository.deleteById(id);
    }

    @Override
    public Posizione getPosizioneById(Long id) {
        return posizioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata"));
    }

    @Override
    public List<Posizione> getPosizioniByHR(Utente hr) {
        return posizioneRepository.findByCreatedByHR(hr);
    }

    @Override
    public Posizione createPosizionePerHR(Posizione posizione, Utente hr) {
        posizione.setCreatedByHR(hr);

        if (posizione.getPubblicataAt() == null) {
            posizione.setPubblicataAt(java.time.LocalDate.now());
        }

        return posizioneRepository.save(posizione);
    }

    // ============================================================
    // CANDIDATI PER POSIZIONE (ordinamento dinamico test / no test)
    // ============================================================

    @Override
    public List<CandidatoPerPosizioneDTO> getCandidatiPerPosizione(Long idPosizione) {

        Posizione posizione = posizioneRepository.findById(idPosizione)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata"));

            boolean haTest = posizione.getIdTest() != null;

        List<Candidatura> candidature =
                candidaturaRepository.findByPosizione_IdPosizione(idPosizione);

        List<CandidatoPerPosizioneDTO> out = new ArrayList<>();
        Integer numeroDomande = null;
        Test test = null;

        // COSTRUZIONE DEL DTO PER L'INVIO DI DATI RIFERITI ALL'UTENTE E ALLA SINGOLA CANDIDATURA
        for (Candidatura c : candidature) {

            if (c.getPosizione().getIdTest() != null) {
                test = testRepository.findById(c.getPosizione().getIdTest()).orElse(null);
                if (test != null) {
                    numeroDomande = test.getNumeroDomande();
                }
            }


            CandidatoPerPosizioneDTO dto = new CandidatoPerPosizioneDTO();
            dto.setIdCandidatura(c.getIdCandidatura());
            dto.setIdCandidato(c.getCandidato().getIdCandidato());

            Utente u = c.getCandidato().getIdUtente();
            dto.setNome(u.getNome());
            dto.setCognome(u.getCognome());
            dto.setEmail(u.getEmail());
            dto.setCvUrl(u.getCvUrl());

            dto.setStato(c.getStato().getCodice());
            dto.setNumeroDomande(numeroDomande);

            // ============================================================
            // SE LA POSIZIONE PREVEDE UN TEST → carica punteggio
            // ============================================================
            if (haTest) {

                List<TentativoTest> tentativi =
                        tentativoTestRepository.findAllByIdCandidatura(c.getIdCandidatura());

                TentativoTest ultimo = tentativi.stream()
                        .filter(t -> t.getCompletatoAt() != null)
                        .max(Comparator.comparing(TentativoTest::getCompletatoAt))
                        .orElse(null);

                if (ultimo != null) {
                    dto.setPunteggioTotale(ultimo.getPunteggioTotale());
                    dto.setEsitoTentativo(
                            ultimo.getIdEsitoTentativo() != null
                                    ? ultimo.getIdEsitoTentativo().getCodice()
                                    : null
                    );
                } else {
                    dto.setPunteggioTotale(0);
                    dto.setEsitoTentativo(null);
                }

            } else {
                // NESSUN TEST → nessun punteggio
                dto.setPunteggioTotale(null);
                dto.setEsitoTentativo(null);
            }

            out.add(dto);
        }

        // ============================================================
        // ORDINAMENTO DINAMICO
        // ============================================================

        if (haTest) {
            // ordina per punteggio DESC
            out.sort(Comparator.comparing(
                    CandidatoPerPosizioneDTO::getPunteggioTotale,
                    Comparator.nullsLast(Comparator.reverseOrder())
            ));
        } else {
            // ordina per data candidatura ASC
            out.sort((a, b) -> {

                Candidatura cA = candidature.stream()
                        .filter(x -> x.getIdCandidatura().equals(a.getIdCandidatura()))
                        .findFirst().get();

                Candidatura cB = candidature.stream()
                        .filter(x -> x.getIdCandidatura().equals(b.getIdCandidatura()))
                        .findFirst().get();

                return cA.getCreatedAt().compareTo(cB.getCreatedAt());
            });
        }

        return out;
    }

    // ============================================================
    // SALVA TOP 5
    // ============================================================

    @Override
    public void salvaTop5(Long idPosizione, Top5Request req) {

        List<Long> top5 = req.getTop5();
        if (top5 == null) top5 = new ArrayList<>();

        List<Candidatura> tutte =
                candidaturaRepository.findByPosizione_IdPosizione(idPosizione);

        StatoCandidatura accettata =
                statoCandidaturaRepository.findByCodice("ACCETTATA")
                        .orElseThrow(() -> new RuntimeException("Stato ACCETTATA non trovato"));

        StatoCandidatura respinta =
                statoCandidaturaRepository.findByCodice("RESPINTA")
                        .orElseThrow(() -> new RuntimeException("Stato RESPINTA non trovato"));

        for (Candidatura c : tutte) {

            if (top5.contains(c.getIdCandidatura())) {
                c.setStato(accettata);
            } else {
                c.setStato(respinta);
            }

            candidaturaRepository.save(c);
        }
    }

    // ============================================================
    // TOP CANDIDATI (limit)
    // ============================================================

    @Override
    public List<CandidatoPerPosizioneDTO> getTopCandidati(Long idPosizione, int limit) {

        List<CandidatoPerPosizioneDTO> tutti = getCandidatiPerPosizione(idPosizione);

        if (limit > 0 && tutti.size() > limit) {
            return tutti.subList(0, limit);
        }

        return tutti;
    }
}
