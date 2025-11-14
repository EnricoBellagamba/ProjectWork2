package com.example.ProjectWork.dto.auth;

public class RegisterRequest {
    private String email;
    private String password;
    private String nome;
    private String cognome;
    private String ruolo;
    private boolean consensoPrivacy;

    public boolean isConsensoPrivacy() {
        return consensoPrivacy;
    }

    public void setConsensoPrivacy(boolean consensoPrivacy) {
        this.consensoPrivacy = consensoPrivacy;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
