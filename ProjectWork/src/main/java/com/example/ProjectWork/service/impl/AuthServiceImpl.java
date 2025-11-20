package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import com.example.ProjectWork.dto.UtenteDto;
import com.example.ProjectWork.exception.*;
import com.example.ProjectWork.model.Ruolo;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.RuoloRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UtenteRepository utenteRepository;
    private final RuoloRepository ruoloRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UtenteRepository utenteRepository,
                           RuoloRepository ruoloRepository,
                           PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.ruoloRepository = ruoloRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse register(RegisterRequest req, MultipartFile cvFile) throws IOException {

        if (req == null) {
            throw new IllegalArgumentException("Dati di registrazione mancanti.");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("La password non può essere nulla o vuota.");
        }

        if (utenteRepository.existsByEmail(req.getEmail())) {
            throw new EmailGiaRegistrataException();
        }

        Ruolo ruolo = ruoloRepository.findByCodice(req.getRuolo())
                .orElseThrow(RuoloNonValidoException::new);

        // Se c'è il CV, lo salvo e metto la URL dentro al DTO
        if (cvFile != null && !cvFile.isEmpty()) {
            String cvUrl = salvaCvSuFileSystem(cvFile);
            req.setCvUrl(cvUrl);
        }

        Utente u = new Utente();
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setNome(req.getNome());
        u.setCognome(req.getCognome());
        u.setConsensoPrivacy(req.isConsensoPrivacy());
        u.setIdRuolo(ruolo);

        // dataNascita: la convertiamo solo se non è vuota
        if (req.getDataNascita() != null && !req.getDataNascita().isBlank()) {
            try {
                u.setDataNascita(LocalDate.parse(req.getDataNascita())); // formato ISO: yyyy-MM-dd
            } catch (DateTimeParseException e) {
                throw new DataNascitaNonValidaException(
                        "Formato dataNascita non valido. Usa il formato yyyy-MM-dd."
                );
            }
        }

        u.setTelefono(req.getTelefono());
        u.setCitta(req.getCitta());

        // lingua: se non viene valorizzata dal frontend, usiamo it-IT
        String lingua = (req.getLingua() != null && !req.getLingua().isBlank())
                ? req.getLingua()
                : "it-IT";
        u.setLingua(lingua);

        // URL del CV (non ha senso hasharla)
        u.setCvUrl(req.getCvUrl());

        // lastLogin: per ora puoi inizializzarlo alla registrazione
        u.setLastLogin(Instant.now());

        Utente saved = utenteRepository.save(u);

        UtenteDto userDto = UtenteDto.fromEntity(saved);
        return new LoginResponse("dummy-access-token", "dummy-refresh-token", userDto);
    }

    //  Qui tengo la logica di salvataggio file
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
    public LoginResponse login(LoginRequest req) {
        Utente utente = utenteRepository.findByEmail(req.getEmail())
                .orElseThrow(UtenteNonTrovatoException::new);

        if (!passwordEncoder.matches(req.getPassword(), utente.getPasswordHash())) {
            throw new PasswordErrataException();
        }

        utente.setLastLogin(Instant.now());
        utenteRepository.save(utente);

        UtenteDto userDto = UtenteDto.fromEntity(utente);
        return new LoginResponse("dummy-access-token", "dummy-refresh-token", userDto);
    }
}
