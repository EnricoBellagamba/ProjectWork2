// src/main/java/com/example/ProjectWork/model/TentativoTest.java
package com.example.ProjectWork.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TENTATIVO_TEST", schema = "dbo")
public class TentativoTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTentativo;

    @ManyToOne
    @JoinColumn(name = "idCandidatura")
    private Candidatura idCandidatura;

    @ManyToOne
    @JoinColumn(name = "idTest")
    private Test idTest;

    @OneToOne
    @JoinColumn(name = "idEsitoTentativo")
    private EsitoTentativo idEsitoTentativo;

    @Column(nullable = false)
    private LocalDateTime iniziatoAt;

    @Column
    private LocalDateTime completatoAt;

    @Column(nullable = false)
    private Integer punteggioTotale;

    @OneToMany(mappedBy = "idTentativo")
    private List<Risposta> risposte;

    // ===================== COSTRUTTORI =====================

    public TentativoTest() {
    }

    public TentativoTest(
            Candidatura idCandidatura,
            Test idTest,
            EsitoTentativo idEsitoTentativo,
            LocalDateTime iniziatoAt,
            LocalDateTime completatoAt,
            Integer punteggioTotale
    ) {
        this.idCandidatura = idCandidatura;
        this.idTest = idTest;
        this.idEsitoTentativo = idEsitoTentativo;
        this.iniziatoAt = iniziatoAt;
        this.completatoAt = completatoAt;
        this.punteggioTotale = punteggioTotale;
    }

    // ===================== GETTER / SETTER =====================

    public Long getIdTentativo() {
        return idTentativo;
    }

    public void setIdTentativo(Long idTentativo) {
        this.idTentativo = idTentativo;
    }

    public Candidatura getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(Candidatura idCandidatura) {
        this.idCandidatura = idCandidatura;
    }

    public Test getIdTest() {
        return idTest;
    }

    public void setIdTest(Test idTest) {
        this.idTest = idTest;
    }

    public EsitoTentativo getIdEsitoTentativo() {
        return idEsitoTentativo;
    }

    public void setIdEsitoTentativo(EsitoTentativo idEsitoTentativo) {
        this.idEsitoTentativo = idEsitoTentativo;
    }

    public LocalDateTime getIniziatoAt() {
        return iniziatoAt;
    }

    public void setIniziatoAt(LocalDateTime iniziatoAt) {
        this.iniziatoAt = iniziatoAt;
    }

    public LocalDateTime getCompletatoAt() {
        return completatoAt;
    }

    public void setCompletatoAt(LocalDateTime completatoAt) {
        this.completatoAt = completatoAt;
    }

    public Integer getPunteggioTotale() {
        return punteggioTotale;
    }

    public void setPunteggioTotale(Integer punteggioTotale) {
        this.punteggioTotale = punteggioTotale;
    }

    public List<Risposta> getRisposte() {
        return risposte;
    }

    public void setRisposte(List<Risposta> risposte) {
        this.risposte = risposte;
    }
}
