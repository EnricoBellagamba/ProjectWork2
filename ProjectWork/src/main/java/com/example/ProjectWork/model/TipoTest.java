package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TIPO_TEST", schema = "dbo")
public class TipoTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoTest;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;


    // getter e setter


    public Long getIdTipoTest() {
        return idTipoTest;
    }

    public void setIdTipoTest(Long idTipoTest) {
        this.idTipoTest = idTipoTest;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
