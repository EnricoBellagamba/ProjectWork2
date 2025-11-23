package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Candidatura;
import com.example.ProjectWork.model.Utente;

import java.util.List;

public interface CandidaturaService {

List<Candidatura> getAllCandidature();
Candidatura createCandidatura(Long idCandidato, Long idPosizione);
List<Candidatura> getCandidatureByUtente(Utente utente);
void deleteCandidatura(Long id);
Candidatura getCandidaturaById(Long id);

}
