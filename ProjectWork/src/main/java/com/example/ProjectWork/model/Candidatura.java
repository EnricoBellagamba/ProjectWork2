package com.example.ProjectWork.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CANDIDATURA" ,schema = "dbo")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidatura;

    @ManyToOne
    @JoinColumn(name = "idCandidato")
    private Candidato candidato;

    @ManyToOne
    @JoinColumn(name = "idPosizione")
    private Posizione posizione;

    @ManyToOne
    @JoinColumn(name = "idStatoCandidatura")
    private StatoCandidatura stato;

    public Long getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(Long idCandidatura) {
        this.idCandidatura = idCandidatura;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public Posizione getPosizione() {
        return posizione;
    }

    public void setPosizione(Posizione posizione) {
        this.posizione = posizione;
    }

    public StatoCandidatura getStato() {
        return stato;
    }

    public void setStato(StatoCandidatura stato) {
        this.stato = stato;
    }

    public LocalDate getCreatedAT() {
        return createdAT;
    }

    public void setCreatedAT(LocalDate createdAT) {
        this.createdAT = createdAT;
    }

    @Column(nullable = false)
    private LocalDate createdAT;
}
