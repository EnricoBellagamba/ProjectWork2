package com.example.ProjectWork.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.Min;

@Entity
@Table(name = "POSIZIONE", schema = "dbo")
public class Posizione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPosizione;

    @Column(nullable = false)
    private String titolo;

    @Column
    private String descrizione;

    @Column
    private String sede;

    @Column
    private String contratto;

    /**
     * RAL: nel DB è la colonna "RAL", nel JSON esponiamo "ral".
     */
    @Column(name = "RAL")
    private Double ral;

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

    @PrePersist
    protected void onCreate() {
        if (this.pubblicataAt == null) {
            this.pubblicataAt = LocalDate.now();
        }
    }

    // GETTER E SETTER -----------------------------------------------------------------------------------------------

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

    public Double getRal() {
        return ral;
    }

    public void setRal(Double ral) {
        this.ral = ral;
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
}
