package com.example.ProjectWork.service;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Interfaccia di servizio dedicata alla gestione delle operazioni di autenticazione
 * e registrazione degli utenti, inclusa la gestione del file CV durante la fase di registrazione.
 */
public interface AuthService {

    /**
     * Registra un nuovo utente utilizzando i dati forniti e il file del curriculum.
     *
     * @param req     l'oggetto contenente i dati necessari alla registrazione
     * @param cvFile  il file del CV allegato dall'utente
     * @return un {@link LoginResponse} contenente le informazioni di autenticazione dell'utente registrato
     * @throws IOException se si verifica un errore durante la lettura o il salvataggio del file
     */
    LoginResponse register(RegisterRequest req, MultipartFile cvFile) throws IOException;

    /**
     * Esegue l'autenticazione dell'utente utilizzando le credenziali fornite.
     *
     * @param req l'oggetto contenente le credenziali di login
     * @return un {@link LoginResponse} contenente il token di autenticazione
     *         e le informazioni essenziali dell'utente autenticato
     */
    LoginResponse login(LoginRequest req);
}
