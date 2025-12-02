package com.example.ProjectWork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "RISPOSTA", schema = "dbo")
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRisposta;

    @Column
    @Max(value = 10, message = "Valore massimo per singola risposta 10")
    @Min(value = 1, message = "Valore minimo per singola risposta 1")
    private Integer punteggioAssegnato;

    @ManyToOne
    @JoinColumn(name = "idTentativo")
    private TentativoTest idTentativo;

    @ManyToOne
    @JoinColumn(name = "idDomanda")
    private Domanda idDomanda;

    @ManyToOne
    @JoinColumn(name = "scelta")
    private Opzione idOpzione; // scelta

    // ===================== COSTRUTTORI =====================

    public Risposta(int punti, Long idTentativo, Long idDomanda, Long idOpzione) {
    }

    public Risposta(
            Integer punteggioAssegnato,
            TentativoTest idTentativo,
            Domanda idDomanda,
            Opzione idOpzione
    ) {
        this.punteggioAssegnato = punteggioAssegnato;
        this.idTentativo = idTentativo;
        this.idDomanda = idDomanda;
        this.idOpzione = idOpzione;
    }

    // ===================== GETTER / SETTER =====================

    public Long getIdRisposta() {
        return idRisposta;
    }

    public void setIdRisposta(Long idRisposta) {
        this.idRisposta = idRisposta;
    }

    public Integer getPunteggioAssegnato() {
        return punteggioAssegnato;
    }

    public void setPunteggioAssegnato(Integer punteggioAssegnato) {
        this.punteggioAssegnato = punteggioAssegnato;
    }

    public TentativoTest getIdTentativo() {
        return idTentativo;
    }

    public void setIdTentativo(TentativoTest idTentativo) {
        this.idTentativo = idTentativo;
    }

    public Domanda getIdDomanda() {
        return idDomanda;
    }

    public void setIdDomanda(Domanda idDomanda) {
        this.idDomanda = idDomanda;
    }

    public Opzione getIdOpzione() {
        return idOpzione;
    }

    public void setIdOpzione(Opzione idOpzione) {
        this.idOpzione = idOpzione;
    }
}
