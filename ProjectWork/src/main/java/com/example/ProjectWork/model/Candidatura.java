package com.example.ProjectWork.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "CANDIDATURA", schema = "dbo")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCandidatura;

    @ManyToOne
    @JoinColumn(name = "idCandidato", nullable = false)
    private Candidato candidato;

    @ManyToOne
    @JoinColumn(name = "idPosizione", nullable = false)
    private Posizione posizione;

    @ManyToOne
    @JoinColumn(name = "idStatoCandidatura", nullable = false)
    private StatoCandidatura stato;

    @Column(nullable = false)
    private LocalDate createdAt;

    //GETTER E SETTER -----------------------------------------------------------------------------------------------

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

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
