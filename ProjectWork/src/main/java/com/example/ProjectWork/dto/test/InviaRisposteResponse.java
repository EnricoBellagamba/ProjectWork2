package com.example.ProjectWork.dto.test;

public class InviaRisposteResponse {
    private Long idTentativo;
    private Integer punteggioTotale;
    private String esito;
    private Double percentuale;

    public InviaRisposteResponse(Long idTentativo, int punteggioTotale, String codiceEsito, Double percentuale) {}

    public InviaRisposteResponse(Long idTentativo, Integer punteggioTotale, Integer punteggioMax, String esito, Double percentuale) {
        this.idTentativo = idTentativo;
        this.punteggioTotale = punteggioTotale;
        this.esito = esito;
        this.percentuale = percentuale;
    }

    // Getters e Setters
    public Long getIdTentativo() {
        return idTentativo;
    }

    public void setIdTentativo(Long idTentativo) {
        this.idTentativo = idTentativo;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public void setPunteggioTotale(Integer punteggioTotale) {
        this.punteggioTotale = punteggioTotale;
    }

    public String getEsito() {
        return esito;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public Double getPercentuale() {
        return percentuale;
    }

    public void setPercentuale(Double percentuale) {
        this.percentuale = percentuale;
    }
}
