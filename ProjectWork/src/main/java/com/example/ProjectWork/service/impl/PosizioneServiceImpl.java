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

    public PosizioneServiceImpl(
            PosizioneRepository posizioneRepository,
            SettoreRepository settoreRepository,
            StatoPosizioneRepository statoPosizioneRepository,
            CandidaturaRepository candidaturaRepository,
            TentativoTestRepository tentativoTestRepository,
            StatoCandidaturaRepository statoCandidaturaRepository
    ) {
        this.posizioneRepository = posizioneRepository;
        this.settoreRepository = settoreRepository;
        this.statoPosizioneRepository = statoPosizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.statoCandidaturaRepository = statoCandidaturaRepository;
    }

    // ============================================================
    // OPERAZIONI GENERICHE SU POSIZIONE
    // ============================================================

    @Override
    public List<Posizione> getAllPosizioni() {
        return posizioneRepository.findAll();
    }

    @Override
    public Posizione createPosizione(Posizione posizione) {

        // default candidature ricevute
        if (posizione.getCandidatureRicevute() == null) {
            posizione.setCandidatureRicevute(0L);
        }

        // settore di default
        if (posizione.getIdSettore() == null) {
            posizione.setIdSettore(
                    settoreRepository.findById(1L)
                            .orElseThrow(() -> new RuntimeException("Settore default (id=1) non trovato"))
            );
        }

        // stato posizione di default
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
            throw new RuntimeException("Posizione non trovata con ID: " + id);
        }
        posizioneRepository.deleteById(id);
    }

    @Override
    public Posizione getPosizioneById(Long id) {
        return posizioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata con ID: " + id));
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
    // CANDIDATI PER POSIZIONE
    // ============================================================

    @Override
    public List<CandidatoPerPosizioneDTO> getCandidatiPerPosizione(Long idPosizione) {

        List<Candidatura> candidature =
                candidaturaRepository.findByPosizione_IdPosizione(idPosizione);

        List<CandidatoPerPosizioneDTO> out = new ArrayList<>();

        for (Candidatura c : candidature) {

            CandidatoPerPosizioneDTO dto = new CandidatoPerPosizioneDTO();

            dto.setIdCandidatura(c.getIdCandidatura());
            dto.setIdCandidato(c.getCandidato().getIdCandidato());

            // Dati utente
            Utente u = c.getCandidato().getIdUtente();
            dto.setNome(u.getNome());
            dto.setCognome(u.getCognome());
            dto.setEmail(u.getEmail());
            dto.setCvUrl(u.getCvUrl());

            // Stato candidatura
            dto.setStato(c.getStato().getCodice());

            // Recupero ultimo tentativo test completato
            List<TentativoTest> tentativi =
                    tentativoTestRepository.findAllByIdCandidatura(c.getIdCandidatura());

            TentativoTest ultimoTentativo = tentativi.stream()
                    .filter(t -> t.getCompletatoAt() != null)
                    .max(Comparator.comparing(TentativoTest::getCompletatoAt))
                    .orElse(null);

            if (ultimoTentativo != null) {
                dto.setPunteggioTotale(ultimoTentativo.getPunteggioTotale());
                dto.setEsitoTentativo(
                        ultimoTentativo.getIdEsitoTentativo() != null
                                ? ultimoTentativo.getIdEsitoTentativo().getCodice()
                                : null
                );
            } else {
                dto.setPunteggioTotale(0);
                dto.setEsitoTentativo(null);
            }

            out.add(dto);
        }

        return out;
    }

    // ============================================================
    // TOP 5 CANDIDATI — HR
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
    // TOP CANDIDATI per POSIZIONE
    // ============================================================

    @Override
    public List<CandidatoPerPosizioneDTO> getTopCandidati(Long idPosizione, int limit) {

        // Recupera tutti i candidati (con punteggio aggiornato)
        List<CandidatoPerPosizioneDTO> tutti = getCandidatiPerPosizione(idPosizione);

        // Ordina DESC per punteggio
        tutti.sort(Comparator.comparing(
                CandidatoPerPosizioneDTO::getPunteggioTotale,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        // Limita
        if (limit > 0 && tutti.size() > limit) {
            return tutti.subList(0, limit);
        }

        return tutti;
    }
}
