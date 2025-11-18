// src/main/java/com/example/ProjectWork/model/EsitoTentativo.java
package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ESITO_TENTATIVO", schema = "dbo")
public class EsitoTentativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEsitoTentativo;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column
    private String descrizione;

    // ===================== COSTRUTTORI =====================

    public EsitoTentativo() {
    }

    public EsitoTentativo(String codice, String descrizione) {
        this.codice = codice;
        this.descrizione = descrizione;
    }

    // ===================== GETTER / SETTER =====================

    public Long getIdEsitoTentativo() {
        return idEsitoTentativo;
    }

    public void setIdEsitoTentativo(Long idEsitoTentativo) {
        this.idEsitoTentativo = idEsitoTentativo;
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
