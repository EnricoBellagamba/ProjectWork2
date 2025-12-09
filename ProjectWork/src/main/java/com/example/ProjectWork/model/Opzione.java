package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "OPZIONE", schema = "dbo")
public class Opzione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idOpzione")
    private Long idOpzione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idDomanda", nullable = false)
    private Domanda domanda;

    @Column(name = "testoOpzione", nullable = false)
    private String testoOpzione;

    @Column(name = "isCorretta", nullable = false)
    private Boolean isCorretta;

    public Opzione() {
        // richiesto da JPA
    }

    public Opzione(Domanda domanda, String testoOpzione, Boolean isCorretta) {
        this.domanda = domanda;
        this.testoOpzione = testoOpzione;
        this.isCorretta = isCorretta;
    }

    //GETTER E SETTER -----------------------------------------------------------------------------------------------

    public Long getIdOpzione() {
        return idOpzione;
    }

    public void setIdOpzione(Long idOpzione) {
        this.idOpzione = idOpzione;
    }

    public Domanda getDomanda() {
        return domanda;
    }

    public void setDomanda(Domanda domanda) {
        this.domanda = domanda;
    }

    public String getTestoOpzione() {
        return testoOpzione;
    }

    public void setTestoOpzione(String testoOpzione) {
        this.testoOpzione = testoOpzione;
    }

    public Boolean getIsCorretta() {
        return isCorretta;
    }

    public void setIsCorretta(Boolean isCorretta) {
        this.isCorretta = isCorretta;
    }
}
