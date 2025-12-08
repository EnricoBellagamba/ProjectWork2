package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "STATO_CANDIDATURA", schema = "dbo")
public class StatoCandidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoCandidatura;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;

    @Column(nullable = false)
    private Boolean isFinale;

    //GETTER E SETTER -----------------------------------------------------------------------------------------------

    public Long getIdStatoCandidatura() {
        return idStatoCandidatura;
    }

    public void setIdStatoCandidatura(Long idStatoCandidatura) {
        this.idStatoCandidatura = idStatoCandidatura;
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

    public Boolean getFinale() {
        return isFinale;
    }

    public void setFinale(Boolean finale) {
        isFinale = finale;
    }
}
