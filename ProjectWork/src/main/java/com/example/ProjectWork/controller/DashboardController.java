package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.dashboard.HrDashboardStatsDto;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PosizioneRepository posizioneRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final TestRepository testRepository;
    private final TentativoTestRepository tentativoTestRepository;
    private final UtenteRepository utenteRepository;

    public DashboardController(
            PosizioneRepository posizioneRepository,
            CandidaturaRepository candidaturaRepository,
            TestRepository testRepository,
            TentativoTestRepository tentativoTestRepository, UtenteRepository utenteRepository
    ) {
        this.posizioneRepository = posizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.testRepository = testRepository;
        this.tentativoTestRepository = tentativoTestRepository;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<HrDashboardStatsDto> getHrDashboardStats(Authentication authentication) {

        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente non autenticato");
        }

        // HR loggato
        String email = authentication.getName();
        Utente hr = utenteRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utente HR non trovato"
                ));

        // Solo posizioni create da questo HR
        long totalePosizioni = posizioneRepository.countByCreatedByHR(hr);

        // Solo candidature relative a posizioni create da questo HR
        long totaleCandidature = candidaturaRepository.countByPosizione_CreatedByHR(hr);

        // Questi due puoi tenerli globali (tutto il DB) oppure filtrarli in futuro
        long totaleTest = testRepository.count();
        long totaleTentativi = tentativoTestRepository.count();

        HrDashboardStatsDto dto = new HrDashboardStatsDto(
                totalePosizioni,
                totaleCandidature,
                totaleTest,
                totaleTentativi
        );

        return ResponseEntity.ok(dto);
    }
}
