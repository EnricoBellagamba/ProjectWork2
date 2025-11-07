package com.example.ProjectWork.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "RUOLO", schema = "dbo")
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRuolo;

    @Column(nullable = false, unique = true)
    private String codice;

    @Column(nullable = false)
    private String descrizione;

    @OneToMany(mappedBy = "idRuolo")
    @JsonManagedReference
    private List<Utente> utenti;

    public Long getIdRuolo() {
        return idRuolo;
    }

    public void setIdRuolo(Long idRuolo) {
        this.idRuolo = idRuolo;
    }

    public String getCodice() {
        return codice;
    }

    public void setCodice(String codice) {
        this.codice = codice;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public List<Utente> getUtenti() {
        return utenti;
    }

    public void setUtenti(List<Utente> utenti) {
        this.utenti = utenti;
    }
}
