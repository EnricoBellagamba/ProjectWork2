package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.UtenteService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final PosizioneRepository posizioneRepository;

    public UtenteServiceImpl(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder,
                             PosizioneRepository posizioneRepository) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.posizioneRepository = posizioneRepository;
    }

    @Override
    public List<Utente> getAllUtenti() {
        return utenteRepository.findAll();
    }

    @Override
    public Utente getUtenteById(Long id) {
        return utenteRepository.findById(id).orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
    }

    @Override
    public Utente createUtente(Utente utente) {
        // Cripta la password solo se è stata fornita
        if (utente.getPasswordHash() != null && !utente.getPasswordHash().isBlank()) {
            String hashedPassword = passwordEncoder.encode(utente.getPasswordHash());
            utente.setPasswordHash(hashedPassword);
        }

        return utenteRepository.save(utente);
    }

    @Override
    public Utente updateUtente(Long id, Utente utente) {
        // Cripta la password solo se è stata fornita
        if (utente.getPasswordHash() != null && !utente.getPasswordHash().isBlank()) {
            String hashedPassword = passwordEncoder.encode(utente.getPasswordHash());
            utente.setPasswordHash(hashedPassword);
        }
        Utente existing = this.getUtenteById(id);

        existing.setNome(utente.getNome());
        existing.setCognome(utente.getCognome());
        existing.setEmail(utente.getEmail());
        existing.setPasswordHash(utente.getPasswordHash());
        existing.setDataNascita(utente.getDataNascita());
        existing.setLastLogin(utente.getLastLogin());
        existing.setTelefono(utente.getTelefono());
        existing.setCitta(utente.getCitta());
        existing.setLingua(utente.getLingua());
        existing.setConsensoPrivacy(utente.getConsensoPrivacy());
        existing.setCvUrl(utente.getCvUrl());
        existing.setCvHash(utente.getCvHash());

        return utenteRepository.save(existing);
    }

    @Override
    public void deleteUtente(Long id) {
        if (!utenteRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        utenteRepository.deleteById(id);
    }

    public boolean verificaPassword(String rawPassword, String hashedPassword) {
        // Confronta la password fornita con quella salvata
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Override
    public void aggiungiPosizionePreferita(Long idUtente, Long idPosizione) {
        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        Posizione posizione = posizioneRepository.findById(idPosizione)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata"));

        utente.getPosizioniPreferite().add(posizione);
        utenteRepository.save(utente);
    }

}
