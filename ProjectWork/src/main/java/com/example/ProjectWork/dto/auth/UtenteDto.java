package com.example.ProjectWork.dto.auth;

import com.example.ProjectWork.model.Utente;

public class UtenteDto {
    private Long idUtente;
    private String email;
    private String nome;
    private String cognome;
    private String ruolo;      // codice ruolo: "CANDIDATO"/"HR"
    private String dataNascita;
    private String telefono;
    private String citta;
    private String lingua;
    private boolean consensoPrivacy;
    private String cvUrl;

    public static UtenteDto fromEntity(Utente u) {
        UtenteDto dto = new UtenteDto();
        dto.idUtente = u.getIdUtente();
        dto.email = u.getEmail();
        dto.nome = u.getNome();
        dto.cognome = u.getCognome();
        dto.ruolo = u.getIdRuolo() != null ? u.getIdRuolo().getCodice() : null;
        dto.dataNascita = u.getDataNascita() != null ? u.getDataNascita().toString() : null;
        dto.telefono = u.getTelefono();
        dto.citta = u.getCitta();
        dto.lingua = u.getLingua();
        dto.consensoPrivacy = Boolean.TRUE.equals(u.getConsensoPrivacy());
        dto.cvUrl = u.getCvUrl();
        return dto;
    }

    // getter e setter
}