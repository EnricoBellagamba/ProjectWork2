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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /** Il ruolo non può essere cambiato da nessuno se non dall'admin del software (NO HR E NO USER) */

    @Override
    public UtenteDto updateUtente(Long id, UpdateProfiloCandidatoRequest req, MultipartFile cvFile) throws IOException {

        Utente existing = this.getUtenteById(id);

        // Nome e cognome
        if (req.getNome() != null && !req.getNome().isBlank()) {
            existing.setNome(req.getNome());
        }
        if (req.getCognome() != null && !req.getCognome().isBlank()) {
            existing.setCognome(req.getCognome());
        }

        // Data di nascita
        if (req.getDataNascita() != null && !req.getDataNascita().isBlank()) {
            try {
                existing.setDataNascita(LocalDate.parse(req.getDataNascita()));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Formato dataNascita non valido. Usa yyyy-MM-dd.");
            }
        }

        // Telefono, città, lingua
        if (req.getTelefono() != null) {
            existing.setTelefono(req.getTelefono());
        }
        if (req.getCitta() != null) {
            existing.setCitta(req.getCitta());
        }
        if (req.getLingua() != null) {
            existing.setLingua(req.getLingua());
        }

        // Nuovo CV
        if (cvFile != null && !cvFile.isEmpty()) {
            String cvUrl = salvaCvSuFileSystem(cvFile);
            existing.setCvUrl(cvUrl);
            // se vuoi resettare l'hash
            existing.setCvHash(null);
        }
        Utente saved = utenteRepository.save(existing);
        return UtenteDto.fromEntity(saved);

    }

    @Override
    public void updatePassword(Long id, UpdatePasswordRequest request) {
        Utente existing = this.getUtenteById(id);

        // Verifica oldPassword
        if (!passwordEncoder.matches(request.getOldPassword(), existing.getPasswordHash())) {
            // Puoi usare la tua PasswordErrataException se ce l'hai
            throw new RuntimeException("La password attuale non è corretta.");
        }

        // Imposta nuova password
        String hashed = passwordEncoder.encode(request.getNewPassword());
        existing.setPasswordHash(hashed);

        utenteRepository.save(existing);
    }

    /**
     * Clonato da AuthServiceImpl: salva il CV su filesystem e restituisce l'URL.
     */
    private String salvaCvSuFileSystem(MultipartFile cvFile) throws IOException {
        if (cvFile == null || cvFile.isEmpty()) {
            return null;
        }

        Path uploadDir = Paths.get("uploads", "cv");
        Files.createDirectories(uploadDir);

        String originalFilename = StringUtils.cleanPath(
                Optional.ofNullable(cvFile.getOriginalFilename()).orElse("cv.pdf")
        );

        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot != -1) {
            ext = originalFilename.substring(dot);
        }

        String newFilename = "cv-" + UUID.randomUUID() + ext;
        Path target = uploadDir.resolve(newFilename);

        Files.copy(cvFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/cv/" + newFilename;
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
