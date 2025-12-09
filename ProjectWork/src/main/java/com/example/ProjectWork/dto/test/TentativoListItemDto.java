package com.example.ProjectWork.dto.test;

import java.time.LocalDateTime;

public class TentativoListItemDto {
    private Long idTentativo;
    private Long idTest;
    private String titoloTest;
    private Integer durataMinuti;
    private Integer punteggioTotale;
    private String esito;
    private LocalDateTime completatoAt;
    private Double percentuale;


    public TentativoListItemDto(Long idTentativo, Long idTest, String titoloTest, Integer durataMinuti, Integer punteggioTotale, String esito, LocalDateTime completatoAt, Double percentuale
    ) {
        this.idTentativo = idTentativo;
        this.idTest = idTest;
        this.titoloTest = titoloTest;
        this.durataMinuti = durataMinuti;
        this.punteggioTotale = punteggioTotale;
        this.esito = esito;
        this.completatoAt = completatoAt;
        this.percentuale = percentuale;
    }

    public TentativoListItemDto(Long idTentativo, Integer punteggioTotale, String titoloTest, Integer durataMinuti, Double percentuale) {

    }

    public TentativoListItemDto(Long idTentativo, Integer punteggioTotale, String codiceEsito, LocalDateTime completatoAt, Double percentuale) {
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

    public LocalDateTime getCompletatoAt() {
        return completatoAt;
    }
}
