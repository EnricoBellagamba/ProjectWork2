package com.example.ProjectWork.service;

import com.example.ProjectWork.model.Utente;

import java.util.List;

public interface UtenteService {
    List<Utente> getAllUtenti();
    Utente getUtenteById(Long id);
    Utente createUtente(Utente utente);
    Utente updateUtente(Long id, Utente utente);
    void deleteUtente(Long id);
}
