package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import com.example.ProjectWork.dto.auth.UtenteDto;
import com.example.ProjectWork.exception.EmailGiaRegistrataException;
import com.example.ProjectWork.exception.PasswordErrataException;
import com.example.ProjectWork.exception.RuoloNonValidoException;
import com.example.ProjectWork.exception.UtenteNonTrovatoException;
import com.example.ProjectWork.model.Ruolo;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.RuoloRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UtenteRepository utenteRepository;
    private final RuoloRepository ruoloRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UtenteRepository utenteRepository,
                       RuoloRepository ruoloRepository,
                       PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.ruoloRepository = ruoloRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse register(RegisterRequest req) {
        if (utenteRepository.existsByEmail(req.getEmail())) {
            throw new EmailGiaRegistrataException();
        }

        Ruolo ruolo = ruoloRepository.findByCodice(req.getRuolo())
                .orElseThrow(RuoloNonValidoException::new);

        Utente u = new Utente();
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setNome(req.getNome());
        u.setCognome(req.getCognome());
        u.setConsensoPrivacy(req.isConsensoPrivacy());
        u.setIdRuolo(ruolo);

        Utente saved = utenteRepository.save(u);

        UtenteDto userDto = UtenteDto.fromEntity(saved);
        // qui in futuro puoi generare JWT veri
        return new LoginResponse("dummy-access-token", "dummy-refresh-token", userDto);
    }

    public LoginResponse login(LoginRequest req) {
        Utente utente = utenteRepository.findByEmail(req.getEmail())
                .orElseThrow(UtenteNonTrovatoException::new);

        if (!passwordEncoder.matches(req.getPassword(), utente.getPasswordHash())) {
            throw new PasswordErrataException();
        }

        UtenteDto userDto = UtenteDto.fromEntity(utente);
        return new LoginResponse("dummy-access-token", "dummy-refresh-token", userDto);
    }
}

