package com.example.ProjectWork.dto.test;

public class TentativoListItemDto {
    private Long idTentativo;
    private Long idTest;
    private String titoloTest;
    private Integer durataMinuti;
    private Integer punteggioTotale;
    private String esito;
    private String completatoAt;

    public TentativoListItemDto(
            Long idTentativo,
            Long idTest,
            String titoloTest,
            Integer durataMinuti,
            Integer punteggioTotale,
            String esito,
            String completatoAt
    ) {
        this.idTentativo = idTentativo;
        this.idTest = idTest;
        this.titoloTest = titoloTest;
        this.durataMinuti = durataMinuti;
        this.punteggioTotale = punteggioTotale;
        this.esito = esito;
        this.completatoAt = completatoAt;
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

    public Integer getDurataMinuti() {
        return durataMinuti;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public String getEsito() {
        return esito;
    }

    public String getCompletatoAt() {
        return completatoAt;
    }
}
