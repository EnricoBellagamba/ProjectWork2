package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.utente.UpdatePasswordRequest;
import com.example.ProjectWork.dto.utente.UpdateProfiloCandidatoRequest;
import com.example.ProjectWork.dto.utente.UtenteDto;
import com.example.ProjectWork.model.Posizione;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.UtenteService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final PosizioneRepository posizioneRepository;

    // Percorsi standard riutilizzabili ovunque
    private static final Path BACKEND_CV_DIR = Paths.get("uploads", "cv");
    private static final Path AI_DATASET_DIR = Paths.get("ai-data", "cv");

    public UtenteServiceImpl(
            UtenteRepository utenteRepository,
            PasswordEncoder passwordEncoder,
            PosizioneRepository posizioneRepository
    ) {
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
        return utenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + id));
    }

    @Override
    public Utente createUtente(Utente utente) {
        if (utente.getPasswordHash() != null && !utente.getPasswordHash().isBlank()) {
            utente.setPasswordHash(passwordEncoder.encode(utente.getPasswordHash()));
        }
        return utenteRepository.save(utente);
    }

    @Override
    public UtenteDto updateUtente(Long id, UpdateProfiloCandidatoRequest req, MultipartFile cvFile)
            throws IOException {

        Utente existing = this.getUtenteById(id);

        if (req.getNome() != null && !req.getNome().isBlank()) existing.setNome(req.getNome());
        if (req.getCognome() != null && !req.getCognome().isBlank()) existing.setCognome(req.getCognome());

        if (req.getDataNascita() != null && !req.getDataNascita().isBlank()) {
            try {
                existing.setDataNascita(LocalDate.parse(req.getDataNascita()));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Formato dataNascita non valido, usa yyyy-MM-dd.");
            }
        }

        if (req.getTelefono() != null) existing.setTelefono(req.getTelefono());
        if (req.getCitta() != null) existing.setCitta(req.getCitta());
        if (req.getLingua() != null) existing.setLingua(req.getLingua());

        if (cvFile != null && !cvFile.isEmpty()) {

            // 1) Salva nel filesystem backend
            String cvUrl = salvaCvSuFileSystem(cvFile);
            existing.setCvUrl(cvUrl);
            existing.setCvHash(null);

            // 2) Copia anche nella cartella dell’AI
            copiaCvNellAIDataset(cvFile, id);
        }

        Utente saved = utenteRepository.save(existing);
        return UtenteDto.fromEntity(saved);
    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        Utente existing = this.getUtenteById(id);

        if (!passwordEncoder.matches(request.getOldPassword(), existing.getPasswordHash())) {
            throw new RuntimeException("La password attuale non è corretta.");
        }

        existing.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        utenteRepository.save(existing);
    }

    private String salvaCvSuFileSystem(MultipartFile cvFile) throws IOException {
        Files.createDirectories(BACKEND_CV_DIR);

        String originalFilename = Optional.ofNullable(cvFile.getOriginalFilename())
                .map(StringUtils::cleanPath)
                .orElse("cv.pdf");

        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot != -1) ext = originalFilename.substring(dot);

        String newFilename = "cv-" + UUID.randomUUID() + ext;
        Path destination = BACKEND_CV_DIR.resolve(newFilename);

        Files.copy(cvFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/cv/" + newFilename;
    }

    /**
     * Copia del CV nella cartella AI
     */
    private void copiaCvNellAIDataset(MultipartFile cvFile, Long idUtente) throws IOException {

        Files.createDirectories(AI_DATASET_DIR);

        String filename = "cv_utente_" + idUtente + ".pdf";
        Path destination = AI_DATASET_DIR.resolve(filename);

        Files.copy(cvFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("✔ CV copiato nel dataset AI → " + destination.toAbsolutePath());
    }

    @Override
    public void deleteUtente(Long id) {
        if (!utenteRepository.existsById(id)) {
            throw new RuntimeException("Utente non trovato con ID: " + id);
        }
        utenteRepository.deleteById(id);
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

    // ============================================================
    // AGGIUNGE AI PREFERITI UNA POSIZIONE
    // ============================================================

    @Override
    public void addFavoritePosition(Long idPosizione, Long idUtente) {

        Posizione posizione = posizioneRepository.findById(idPosizione)
                .orElseThrow(() -> new RuntimeException("Posizione non trovata"));

        Utente utente = utenteRepository.findById(idUtente)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if(!utente.getPosizioniPreferite().contains(posizione)){
            utente.getPosizioniPreferite().add(posizione);
            utenteRepository.save(utente);
        }

    }

    @Override

    public void removeFavoritePosition(Long idPosizione, Long idUtente) {

        Utente utente = utenteRepository.findById(idUtente).orElseThrow(() -> new  RuntimeException("Utente non trovato"));

        utente.getPosizioniPreferite().removeIf(p -> p.getIdPosizione().equals(idPosizione));
        utenteRepository.save(utente);

    }


    @Override

    public List<Posizione> getPosizioniPreferiteByIdUtente(Long idUtente) {

        Utente utente = utenteRepository.findById(idUtente).orElseThrow(() -> new RuntimeException("Utente non trovato"));

        return utente.getPosizioniPreferite();

    }


}
