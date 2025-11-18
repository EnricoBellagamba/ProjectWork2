package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "CANDIDATO", schema = "dbo")
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidato;

    @OneToOne
    @JoinColumn(name = "idUtente")
    private Utente idUtente;

    @Column(nullable = false)
    private Boolean isActive;


    public Long getIdCandidato() {
        return idCandidato;
    }

    public void setIdCandidato(Long idCandidato) {
        this.idCandidato = idCandidato;
    }

    public Utente getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Utente idUtente) {
        this.idUtente = idUtente;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}