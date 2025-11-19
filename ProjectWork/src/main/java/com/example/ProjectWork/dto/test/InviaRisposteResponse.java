package com.example.ProjectWork.dto.test;

public class InviaRisposteResponse {
    private Long idTentativo;
    private Integer punteggioTotale;
    private Integer punteggioMax;
    private String esito;

    public InviaRisposteResponse(
            Long idTentativo,
            Integer punteggioTotale,
            Integer punteggioMax,
            String esito
    ) {
        this.idTentativo = idTentativo;
        this.punteggioTotale = punteggioTotale;
        this.punteggioMax = punteggioMax;
        this.esito = esito;
    }

    public Long getIdTentativo() {
        return idTentativo;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public Integer getPunteggioMax() {
        return punteggioMax;
    }

    public String getEsito() {
        return esito;
    }
}
