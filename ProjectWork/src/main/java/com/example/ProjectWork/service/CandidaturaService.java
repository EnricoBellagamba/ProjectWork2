package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Candidatura;

import java.util.List;

public interface CandidaturaService {

List<Candidatura> getAllCandidature();
Candidatura createCandidatura(Candidatura candidatura);
void deleteCandidatura(Long id);
Candidatura getCandidaturaById(Long id);
Candidatura updateCandidatura(Long id, Candidatura candidatura);

}
