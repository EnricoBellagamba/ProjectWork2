package com.example.ProjectWork.dto.test;

import java.util.List;

public class StrutturaTestResponse {
    private Long idTest;
    private String titolo;
    private String descrizione;
    private Integer durataMinuti;
    private Integer numeroDomande;
    private Integer punteggioMax;
    private Integer punteggioMin;
    private String tipo;
    private List<DomandaDto> domande;

    public StrutturaTestResponse(
            Long idTest,
            String titolo,
            String descrizione,
            Integer durataMinuti,
            Integer numeroDomande,
            Integer punteggioMax,
            Integer punteggioMin,
            String tipo,
            List<DomandaDto> domande
    ) {
        this.idTest = idTest;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.durataMinuti = durataMinuti;
        this.numeroDomande = numeroDomande;
        this.punteggioMax = punteggioMax;
        this.punteggioMin = punteggioMin;
        this.tipo = tipo;
        this.domande = domande;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public Integer getDurataMinuti() {
        return durataMinuti;
    }

    public Integer getNumeroDomande() {
        return numeroDomande;
    }

    public Integer getPunteggioMax() {
        return punteggioMax;
    }

    public Integer getPunteggioMin() {
        return punteggioMin;
    }

    public String getTipo() {
        return tipo;
    }

    public List<DomandaDto> getDomande() {
        return domande;
    }
}
