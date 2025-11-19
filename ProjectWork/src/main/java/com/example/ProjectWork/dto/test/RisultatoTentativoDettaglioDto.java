package com.example.ProjectWork.dto.test;

public class RisultatoTentativoDettaglioDto {
    private Long idTentativo;
    private Long idTest;
    private String titoloTest;
    private Integer punteggioTotale;
    private Integer punteggioMax;
    private Integer punteggioMin;
    private String esito;
    private String completatoAt;
    private Integer durataUsataMinuti;
    private Integer numeroDomande;
    private Integer numeroCorrette;
    private Integer numeroErrate;
    private Integer numeroNonRisposte;

    public RisultatoTentativoDettaglioDto(
            Long idTentativo,
            Long idTest,
            String titoloTest,
            Integer punteggioTotale,
            Integer punteggioMax,
            Integer punteggioMin,
            String esito,
            String completatoAt,
            Integer durataUsataMinuti,
            Integer numeroDomande,
            Integer numeroCorrette,
            Integer numeroErrate,
            Integer numeroNonRisposte
    ) {
        this.idTentativo = idTentativo;
        this.idTest = idTest;
        this.titoloTest = titoloTest;
        this.punteggioTotale = punteggioTotale;
        this.punteggioMax = punteggioMax;
        this.punteggioMin = punteggioMin;
        this.esito = esito;
        this.completatoAt = completatoAt;
        this.durataUsataMinuti = durataUsataMinuti;
        this.numeroDomande = numeroDomande;
        this.numeroCorrette = numeroCorrette;
        this.numeroErrate = numeroErrate;
        this.numeroNonRisposte = numeroNonRisposte;
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

    public Integer getPunteggioMax() {
        return punteggioMax;
    }

    public Integer getPunteggioMin() {
        return punteggioMin;
    }

    public String getEsito() {
        return esito;
    }

    public String getCompletatoAt() {
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
}
