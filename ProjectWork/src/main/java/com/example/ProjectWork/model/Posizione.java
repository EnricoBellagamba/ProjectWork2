package com.example.ProjectWork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "POSIZIONE", schema = "dbo")
public class Posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long idPosizione;

    @Column(nullable = false)
    private String titolo;

    @Column
    private String descrizione;

    @Column
    private String sede;

    @Column
    private String contratto;

    // RAL annua lorda
    @Column(name = "RAL")
    private Double RAL;

    @Column(nullable = false)
    @Min(value = 0)
    private Long candidatureRicevute;

    @Column
    private LocalDate pubblicataAt;

    @Column
    private LocalDate chiusaAt;

    @ManyToOne
    @JoinColumn(name = "createdByHR")
    private Utente createdByHR;

    @ManyToOne
    @JoinColumn(name = "idStatoPosizione")
    private StatoPosizione idStatoPosizione;

    @ManyToOne
    @JoinColumn(name = "idSettore")
    private Settore idSettore;

    @ManyToMany(mappedBy = "posizioniPreferite")
    private List<Utente> utentiCheHannoPreferito = new ArrayList<>();

    // ID del test associato alla posizione (FK su TEST.idTest)
    @Column(name = "idTest")
    private Long idTest;

    @PrePersist
    protected void onCreate() {
        if (this.pubblicataAt == null) {
            this.pubblicataAt = LocalDate.now();
        }
        if (this.candidatureRicevute == null) {
            this.candidatureRicevute = 0L;
        }
    }

    // GETTER E SETTER -------------------------

    public Long getIdPosizione() {
        return idPosizione;
    }

    public void setIdPosizione(Long idPosizione) {
        this.idPosizione = idPosizione;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getContratto() {
        return contratto;
    }

    public void setContratto(String contratto) {
        this.contratto = contratto;
    }

    public Double getRAL() {
        return RAL;
    }

    public void setRAL(Double RAL) {
        this.RAL = RAL;
    }

    public Long getCandidatureRicevute() {
        return candidatureRicevute;
    }

    public void setCandidatureRicevute(Long candidatureRicevute) {
        this.candidatureRicevute = candidatureRicevute;
    }

    public LocalDate getPubblicataAt() {
        return pubblicataAt;
    }

    public void setPubblicataAt(LocalDate pubblicataAt) {
        this.pubblicataAt = pubblicataAt;
    }

    public LocalDate getChiusaAt() {
        return chiusaAt;
    }

    public void setChiusaAt(LocalDate chiusaAt) {
        this.chiusaAt = chiusaAt;
    }

    public Utente getCreatedByHR() {
        return createdByHR;
    }

    public void setCreatedByHR(Utente createdByHR) {
        this.createdByHR = createdByHR;
    }

    public StatoPosizione getIdStatoPosizione() {
        return idStatoPosizione;
    }

    public void setIdStatoPosizione(StatoPosizione idStatoPosizione) {
        this.idStatoPosizione = idStatoPosizione;
    }

    public Settore getIdSettore() {
        return idSettore;
    }

    public void setIdSettore(Settore idSettore) {
        this.idSettore = idSettore;
    }

    public List<Utente> getUtentiCheHannoPreferito() {
        return utentiCheHannoPreferito;
    }

    public void setUtentiCheHannoPreferito(List<Utente> utentiCheHannoPreferito) {
        this.utentiCheHannoPreferito = utentiCheHannoPreferito;
    }

    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
    }
}
