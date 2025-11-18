package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Candidato;

import java.util.List;

public interface CandidatoService {

    List<Candidato> getAllCandidati();
    Candidato createCandidato(Candidato candidato);
    void deleteCandidato(Long id);
    Candidato getCandidatoById (Long id);
    Candidato updateCandidato(Long id, Candidato candidato);

}
