package com.example.ProjectWork.dto;

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

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public String getLingua() {
        return lingua;
    }

    public void setLingua(String lingua) {
        this.lingua = lingua;
    }

    public boolean isConsensoPrivacy() {
        return consensoPrivacy;
    }

    public void setConsensoPrivacy(boolean consensoPrivacy) {
        this.consensoPrivacy = consensoPrivacy;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }
}