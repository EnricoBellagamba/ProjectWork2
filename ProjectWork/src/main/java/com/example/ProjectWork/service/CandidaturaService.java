package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Candidatura;

import java.util.List;

public interface CandidaturaService {

List<Candidatura> getAllCandidature();
Candidatura createCandidatura(Long idCandidato, Long idPosizione);
List<Candidatura> getCandidatureByCandidato(Long idCandidato);
void deleteCandidatura(Long id);
Candidatura getCandidaturaById(Long id);

}
