package com.example.ProjectWork.dto.test;

import java.time.LocalDateTime;

public class RisultatoTentativoDettaglioDto {
    private Long idTentativo;
    private Long idTest;
    private String titoloTest;
    private Double percentuale;
    private Integer punteggioTotale;
    private Integer punteggioMin;
    private String esito;
    private LocalDateTime completatoAt;
    private Integer durataUsataMinuti;
    private Integer numeroDomande;
    private Integer numeroCorrette;
    private Integer numeroErrate;
    private Integer numeroNonRisposte;

    public RisultatoTentativoDettaglioDto() {

    }

    public RisultatoTentativoDettaglioDto(
            Long idTentativo,
            Long idTest,
            String titoloTest,
            Double percentuale,
            Integer punteggioTotale,
            Integer punteggioMin,
            String esito,
            LocalDateTime completatoAt,
            Integer durataUsataMinuti,
            Integer numeroDomande,
            Integer numeroCorrette,
            Integer numeroErrate,
            Integer numeroNonRisposte
    ) {
        this.idTentativo = idTentativo;
        this.idTest = idTest;
        this.titoloTest = titoloTest;
        this.percentuale=percentuale;
        this.punteggioTotale = punteggioTotale;
        this.punteggioMin = punteggioMin;
        this.esito = esito;
        this.completatoAt = completatoAt;
        this.durataUsataMinuti = durataUsataMinuti;
        this.numeroDomande = numeroDomande;
        this.numeroCorrette = numeroCorrette;
        this.numeroErrate = numeroErrate;
        this.numeroNonRisposte = numeroNonRisposte;
    }

    public Double getPercentuale() {
        return percentuale;
    }

    public Long getIdTentativo() {
        return idTentativo;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getTitoloTest() {
        return titoloTest;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public Integer getPunteggioMin() {
        return punteggioMin;
    }

    public String getEsito() {
        return esito;
    }

    public LocalDateTime getCompletatoAt() {
        return completatoAt;
    }

    public Integer getDurataUsataMinuti() {
        return durataUsataMinuti;
    }

    public Integer getNumeroDomande() {
        return numeroDomande;
    }

    public Integer getNumeroCorrette() {
        return numeroCorrette;
    }

    public Integer getNumeroErrate() {
        return numeroErrate;
    }

    public Integer getNumeroNonRisposte() {
        return numeroNonRisposte;
    }

    public void setIdTentativo(Long idTentativo) {
        this.idTentativo = idTentativo;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
    }

    public void setTitoloTest(String titoloTest) {
        this.titoloTest = titoloTest;
    }

    public void setPercentuale(Double percentuale) {
        this.percentuale = percentuale;
    }

    public void setPunteggioTotale(Integer punteggioTotale) {
        this.punteggioTotale = punteggioTotale;
    }

    public void setPunteggioMin(Integer punteggioMin) {
        this.punteggioMin = punteggioMin;
    }

    public void setEsito(String esito) {
        this.esito = esito;
    }

    public void setCompletatoAt(LocalDateTime completatoAt) {
        this.completatoAt = completatoAt;
    }

    public void setDurataUsataMinuti(Integer durataUsataMinuti) {
        this.durataUsataMinuti = durataUsataMinuti;
    }

    public void setNumeroDomande(Integer numeroDomande) {
        this.numeroDomande = numeroDomande;
    }

    public void setNumeroCorrette(Integer numeroCorrette) {
        this.numeroCorrette = numeroCorrette;
    }

    public void setNumeroErrate(Integer numeroErrate) {
        this.numeroErrate = numeroErrate;
    }

    public void setNumeroNonRisposte(Integer numeroNonRisposte) {
        this.numeroNonRisposte = numeroNonRisposte;
    }
}
