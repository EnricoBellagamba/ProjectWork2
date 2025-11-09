package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "STATO_POSIZIONE", schema = "dbo")
public class StatoPosizione {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStatoPosizione;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;

    public Long getIdStatoPosizione() {
        return idStatoPosizione;
    }

    public void setIdStatoPosizione(Long idStatoPosizione) {
        this.idStatoPosizione = idStatoPosizione;
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