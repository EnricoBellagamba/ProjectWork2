package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "SETTORE", schema = "dbo")
public class Settore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSettore;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column(nullable = false)
    private String nome;

    @Column
    private String descrizione;

    public Long getIdSettore() {
        return idSettore;
    }

    public void setIdSettore(Long idSettore) {
        this.idSettore = idSettore;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
