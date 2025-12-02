package com.example.ProjectWork.dto.test;

public class InviaRisposteResponse {
    private Long idTentativo;
    private Integer punteggioTotale;
    private String esito;

    public InviaRisposteResponse(
            Long idTentativo,
            Integer punteggioTotale,
            String esito
    ) {
        this.idTentativo = idTentativo;
        this.punteggioTotale = punteggioTotale;
        this.esito = esito;
    }

    public Long getIdTentativo() {
        return idTentativo;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public String getEsito() {
        return esito;
    }
}
