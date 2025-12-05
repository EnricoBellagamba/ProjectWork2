package com.example.ProjectWork.service.impl;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import com.example.ProjectWork.dto.utente.UtenteDto;
import com.example.ProjectWork.exception.*;
import com.example.ProjectWork.model.*;
import com.example.ProjectWork.repository.*;
import com.example.ProjectWork.security.JwtService;
import com.example.ProjectWork.service.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UtenteRepository utenteRepository;
    private final RuoloRepository ruoloRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailBloccataRepository emailBloccataRepository;
    private final CandidatoRepository candidatoRepository;

    public AuthServiceImpl(
            UtenteRepository utenteRepository,
            RuoloRepository ruoloRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmailBloccataRepository emailBloccataRepository,
            CandidatoRepository candidatoRepository
    ) {
        this.utenteRepository = utenteRepository;
        this.ruoloRepository = ruoloRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.emailBloccataRepository = emailBloccataRepository;
        this.candidatoRepository = candidatoRepository;
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest req, MultipartFile cvFile) throws IOException {

        Optional<EmailBloccata> opt = emailBloccataRepository.findByEmail(req.getEmail());
        if (opt.isPresent()) {
            EmailBloccata bloccata = opt.get();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime riabilitazione = bloccata.getDataRiabilitazione();

            if (riabilitazione == null) {
                throw new EmailBloccataException("La mail è bloccata.");
            }
            if (now.isBefore(riabilitazione)) {
                throw new EmailBloccataException("La mail è temporaneamente bloccata.");
            }
            if (!now.isBefore(riabilitazione)) {
                emailBloccataRepository.delete(bloccata);
            }
        }

        if (req == null)
            throw new IllegalArgumentException("Dati mancanti.");

        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new IllegalArgumentException("Password vuota.");

        if (utenteRepository.existsByEmail(req.getEmail()))
            throw new EmailGiaRegistrataException();

        Ruolo ruolo = ruoloRepository.findByCodice(req.getRuolo())
                .orElseThrow(RuoloNonValidoException::new);

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
        u.setTelefono(req.getTelefono());
        u.setCitta(req.getCitta());
        u.setLingua((req.getLingua() != null && !req.getLingua().isBlank()) ? req.getLingua() : "it-IT");
        u.setCvUrl(req.getCvUrl());
        u.setLastLogin(Instant.now());

        if (req.getDataNascita() != null && !req.getDataNascita().isBlank()) {
            try {
                u.setDataNascita(LocalDate.parse(req.getDataNascita()));
            } catch (DateTimeParseException e) {
                throw new DataNascitaNonValidaException("Formato data non valido.");
            }
        }

        Utente savedUser = utenteRepository.save(u);

        if (ruolo.getCodice().equalsIgnoreCase("CANDIDATO")) {
            Candidato c = new Candidato();
            c.setIdUtente(savedUser);
            c.setActive(true);
            candidatoRepository.save(c);
        }

        UtenteDto dto = UtenteDto.fromEntity(savedUser);
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return new LoginResponse(accessToken, refreshToken, dto);
    }

    @Override
    public LoginResponse login(LoginRequest req) {

        Utente utente = utenteRepository.findByEmail(req.getEmail())
                .orElseThrow(UtenteNonTrovatoException::new);

        if (!passwordEncoder.matches(req.getPassword(), utente.getPasswordHash()))
            throw new PasswordErrataException();

        utente.setLastLogin(Instant.now());
        utenteRepository.save(utente);

        UtenteDto dto = UtenteDto.fromEntity(utente);
        String accessToken = jwtService.generateAccessToken(utente);
        String refreshToken = jwtService.generateRefreshToken(utente);

        return new LoginResponse(accessToken, refreshToken, dto);
    }

    private String salvaCvSuFileSystem(MultipartFile cvFile) throws IOException {

        if (cvFile == null || cvFile.isEmpty()) return null;

        Path uploadDir = Paths.get("uploads", "cv");
        Files.createDirectories(uploadDir);

        String originalFilename = StringUtils.cleanPath(
                Optional.ofNullable(cvFile.getOriginalFilename()).orElse("cv.pdf")
        );

        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot != -1) ext = originalFilename.substring(dot);

        String newFilename = "cv-" + UUID.randomUUID() + ext;

        Path target = uploadDir.resolve(newFilename);
        Files.copy(cvFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/cv/" + newFilename;
    }
}
