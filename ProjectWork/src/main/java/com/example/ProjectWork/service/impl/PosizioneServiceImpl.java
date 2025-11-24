package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.StatoPosizione;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.SettoreRepository;
import com.example.ProjectWork.repository.StatoPosizioneRepository;
import com.example.ProjectWork.service.PosizioneService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PosizioneServiceImpl implements PosizioneService {

    private final PosizioneRepository posizioneRepository;
    private final SettoreRepository settoreRepository;
    private final StatoPosizioneRepository statoPosizioneRepository;

    public PosizioneServiceImpl(PosizioneRepository posizioneRepository, SettoreRepository settoreRepository, StatoPosizioneRepository statoPosizioneRepository) {
        this.posizioneRepository = posizioneRepository;
        this.settoreRepository = settoreRepository;
        this.statoPosizioneRepository = statoPosizioneRepository;
    }

    @Override
    public List<Posizione> getAllPosizioni() {
        return posizioneRepository.findAll();
    }

    // usato  lato pubblico / seed
    @Override
    public Posizione createPosizione(Posizione posizione) {
        // candidatureRicevute di default 0
        if (posizione.getCandidatureRicevute() == null) {
            posizione.setCandidatureRicevute(0L);
        }

        // Settore di default (ID 1) – per il prototipo va benissimo
        if (posizione.getIdSettore() == null) {
            posizione.setIdSettore(
                    settoreRepository.findById(1L)
                            .orElseThrow(() -> new RuntimeException("Settore default (id=1) non trovato"))
            );
        }

        // Se non arriva dallo JSON, mettiamo lo stato "APERTA" di default
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
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        posizioneRepository.deleteById(id);
    }

    @Override
    public Posizione getPosizioneById(Long id) {
        return posizioneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posizione non trovato con ID: " + id));
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

}
