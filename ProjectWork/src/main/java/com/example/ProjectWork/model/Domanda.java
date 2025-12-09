package com.example.ProjectWork.model;

import jakarta.persistence.*;

@Entity
@Table(name = "DOMANDA", schema = "dbo")
public class Domanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDomanda")
    private Long idDomanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idTest", nullable = false)
    private Test test;

    @Column(nullable = false)
    private String testo;

    public Domanda() {
        // costruttore vuoto richiesto da JPA
    }

    public Domanda(Test test, String testo) {
        this.test = test;
        this.testo = testo;
    }

    //GETTER E SETTER -----------------------------------------------------------------------------------------------

    public Long getIdDomanda() {
        return idDomanda;
    }

    public void setIdDomanda(Long idDomanda) {
        this.idDomanda = idDomanda;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}
