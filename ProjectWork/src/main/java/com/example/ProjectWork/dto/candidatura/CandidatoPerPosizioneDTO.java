package com.example.ProjectWork.dto.candidatura;

public class CandidatoPerPosizioneDTO {

    private Long idCandidatura;
    private Long idCandidato;

    private String nome;
    private String cognome;
    private String email;

    private String cvUrl;

    private Integer punteggioTotale;
    private String esitoTentativo;

    private String stato;
    private Integer numeroDomande;


    public CandidatoPerPosizioneDTO(Long idCandidatura, Long idCandidato, String nome, String cognome, String email, String cvUrl, Integer punteggioTotale, String esitoTentativo, String stato, Integer numeroDomande) {
        this.idCandidatura = idCandidatura;
        this.idCandidato = idCandidato;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.cvUrl = cvUrl;
        this.punteggioTotale = punteggioTotale;
        this.esitoTentativo = esitoTentativo;
        this.stato = stato;
        this.numeroDomande = numeroDomande;
    }


    public CandidatoPerPosizioneDTO() {}

    public Long getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(Long idCandidatura) {
        this.idCandidatura = idCandidatura;
    }

    public Long getIdCandidato() {
        return idCandidato;
    }

    public void setIdCandidato(Long idCandidato) {
        this.idCandidato = idCandidato;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCvUrl() {
        return cvUrl;
    }

    public void setCvUrl(String cvUrl) {
        this.cvUrl = cvUrl;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public void setPunteggioTotale(Integer punteggioTotale) {
        this.punteggioTotale = punteggioTotale;
    }

    public String getEsitoTentativo() {
        return esitoTentativo;
    }

    public void setEsitoTentativo(String esitoTentativo) {
        this.esitoTentativo = esitoTentativo;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public Integer getNumeroDomande() {
        return numeroDomande;
    }

    public void setNumeroDomande(Integer numeroDomande) {
        this.numeroDomande = numeroDomande;
    }

}
