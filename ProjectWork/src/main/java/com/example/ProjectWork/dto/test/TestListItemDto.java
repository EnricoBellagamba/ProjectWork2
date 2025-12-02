package com.example.ProjectWork.dto.test;

public class TestListItemDto {
    private Long idTest;
    private String titolo;
    private String tipo; // opzionale
    private Integer durataMinuti;
    private String descrizione;

    public TestListItemDto(
            Long idTest,
            String titolo,
            String tipo,
            Integer durataMinuti,
            String descrizione
    ) {
        this.idTest = idTest;
        this.titolo = titolo;
        this.tipo = tipo;
        this.durataMinuti = durataMinuti;
        this.descrizione = descrizione;
    }

    public Long getIdTest() {
        return idTest;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getDurataMinuti() {
        return durataMinuti;
    }

    public String getDescrizione() {
        return descrizione;
    }

}
