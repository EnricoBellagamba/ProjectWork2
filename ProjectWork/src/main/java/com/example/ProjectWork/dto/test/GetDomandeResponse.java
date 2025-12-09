package com.example.ProjectWork.dto.test;

import java.util.List;

public class GetDomandeResponse {
    private Long idTentativo;
    private Long idTest;
    private String titoloTest;
    private Integer durataMinuti;
    private List<DomandaDto> domande;

    public GetDomandeResponse(Long idTentativo, Long idTest, String titoloTest, Integer durataMinuti, List<DomandaDto> domande
    ) {
        this.idTentativo = idTentativo;
        this.idTest = idTest;
        this.titoloTest = titoloTest;
        this.durataMinuti = durataMinuti;
        this.domande = domande;
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

    public List<DomandaDto> getDomande() {
        return domande;
    }
}
