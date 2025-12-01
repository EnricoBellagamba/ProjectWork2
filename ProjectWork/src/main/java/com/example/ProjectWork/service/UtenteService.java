package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.utente.UpdatePasswordRequest;
import com.example.ProjectWork.dto.utente.UpdateProfiloCandidatoRequest;
import com.example.ProjectWork.dto.utente.UtenteDto;
import com.example.ProjectWork.model.Utente;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UtenteService {
    List<Utente> getAllUtenti();
    Utente getUtenteById(Long id);
    Utente createUtente(Utente utente);

    UtenteDto updateUtente(Long id, UpdateProfiloCandidatoRequest req, MultipartFile cvFile) throws IOException;
    void updatePassword(Long id, UpdatePasswordRequest request);

    void deleteUtente(Long id);
    void aggiungiPosizionePreferita(Long idUtente, Long idPosizione);
}
