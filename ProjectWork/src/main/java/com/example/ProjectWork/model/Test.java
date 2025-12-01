package com.example.ProjectWork.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "TEST", schema = "dbo")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTest;

    @Column(nullable = false)
    private String titolo;

    @Column
    private String descrizione;

    @Max(value = 60, message = "I minuti non possono superare i 60")
    @Column(nullable = false)
    private Integer durataMinuti;

    @Max(value = 20, message = "Domande massime per test 20")
    @Column(nullable = false)
    private Integer numeroDomande;

    @Max(value = 100, message = "Punteggio massimo per test 100")
    @Column(nullable = false)
    private Integer punteggioMax;

    @Min(value = 0, message = "Punteggio minimo per test 0")
    @Column(nullable = false)
    private Integer punteggioMin;

    @Column(nullable = false)
    private Boolean isAttivo;

    @ManyToOne
    @JoinColumn(name = "idTipoTest")
    private TipoTest tipoTest;

    public Long getIdTest() {
        return idTest;
    }

    public void setIdTest(Long idTest) {
        this.idTest = idTest;
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

    public Integer getDurataMinuti() {
        return durataMinuti;
    }

    public void setDurataMinuti(Integer durataMinuti) {
        this.durataMinuti = durataMinuti;
    }

    public Integer getNumeroDomande() {
        return numeroDomande;
    }

    public void setNumeroDomande(Integer numeroDomande) {
        this.numeroDomande = numeroDomande;
    }

    public Integer getPunteggioMax() {
        return punteggioMax;
    }

    public void setPunteggioMax(Integer punteggioMax) {
        this.punteggioMax = punteggioMax;
    }

    public Integer getPunteggioMin() {
        return punteggioMin;
    }

    public void setPunteggioMin(Integer punteggioMin) {
        this.punteggioMin = punteggioMin;
    }

    public Boolean getAttivo() {
        return isAttivo;
    }

    public void setAttivo(Boolean attivo) {
        isAttivo = attivo;
    }

    public TipoTest getTipoTest() {
        return tipoTest;
    }

    public void setTipoTest(TipoTest tipoTest) {
        this.tipoTest = tipoTest;
    }
}
