package com.example.ProjectWork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "RISPOSTA", schema = "dbo",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"idTentativo", "idDomanda"})
        }
)
public class Risposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRisposta;

    @Column
    @Max(value = 10, message = "Valore massimo per singola risposta: 10")
    @Min(value = 0, message = "Valore minimo per singola risposta: 0")
    private Integer punteggioAssegnato;

    // Tentativo associato
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTentativo", nullable = false)
    private TentativoTest idTentativo;

    // Domanda associata
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idDomanda", nullable = false)
    private Domanda idDomanda;

    // Opzione scelta (può essere null se non risponde)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scelta")
    private Opzione idOpzione;

    // =====================================================
    //                   COSTRUTTORI
    // =====================================================

    // Costruttore vuoto JPA
    public Risposta() {
    }

    // Costruttore completo
    public Risposta(Integer punteggioAssegnato,
                    TentativoTest tentativo,
                    Domanda domanda,
                    Opzione opzione) {
        this.punteggioAssegnato = punteggioAssegnato;
        this.idTentativo = tentativo;
        this.idDomanda = domanda;
        this.idOpzione = opzione;
    }

    // =====================================================
    //                   GETTER / SETTER
    // =====================================================

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
