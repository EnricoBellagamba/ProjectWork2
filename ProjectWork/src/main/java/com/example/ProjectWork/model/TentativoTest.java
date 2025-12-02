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

    @Column(name = "idCandidatura")
    private Long idCandidatura;

    @Column(name = "idTest")
    private Long idTest;

    @Column(nullable = false)
    private LocalDateTime iniziatoAt;

    @Column
    private LocalDateTime completatoAt;

    @Column(nullable = false)
    private Integer punteggioTotale;

    @OneToOne
    @JoinColumn(name = "idEsitoTentativo")
    private EsitoTentativo idEsitoTentativo;

    // ===================== COSTRUTTORI =====================

    public TentativoTest() {
    }

    public TentativoTest(
            Long idCandidatura,
            Long idTest,
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

    public Long getIdCandidatura() {
        return idCandidatura;
    }

    public void setIdCandidatura(Long idCandidatura) {
        this.idCandidatura = idCandidatura;
    }

    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
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

    public String getCodiceEsito() {

        return "";
    }

    public void setCodiceEsito(Object o) {
    }
}
