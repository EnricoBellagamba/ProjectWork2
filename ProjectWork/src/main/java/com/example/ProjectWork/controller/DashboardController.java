package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.dashboard.HrDashboardStatsDto;
import com.example.ProjectWork.repository.CandidaturaRepository;
import com.example.ProjectWork.repository.PosizioneRepository;
import com.example.ProjectWork.repository.TestRepository;
import com.example.ProjectWork.repository.TentativoTestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final PosizioneRepository posizioneRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final TestRepository testRepository;
    private final TentativoTestRepository tentativoTestRepository;

    public DashboardController(
            PosizioneRepository posizioneRepository,
            CandidaturaRepository candidaturaRepository,
            TestRepository testRepository,
            TentativoTestRepository tentativoTestRepository
    ) {
        this.posizioneRepository = posizioneRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.testRepository = testRepository;
        this.tentativoTestRepository = tentativoTestRepository;
    }

    @GetMapping("/hr")
    public ResponseEntity<HrDashboardStatsDto> getHrDashboardStats() {

        long totalePosizioni = posizioneRepository.count();
        long totaleCandidature = candidaturaRepository.count();
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
