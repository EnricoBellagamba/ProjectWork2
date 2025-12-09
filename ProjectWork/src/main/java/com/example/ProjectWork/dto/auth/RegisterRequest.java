package com.example.ProjectWork.dto.auth;

public class RegisterRequest {

    private String email;
    private String password;
    private String nome;
    private String cognome;
    private String ruolo;
    private String telefono;
    private String dataNascita;
    private String citta;
    private String lingua;
    private boolean consensoPrivacy;
    private String cvUrl;

    public RegisterRequest() {
    }

    // GETTER & SETTER

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
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
