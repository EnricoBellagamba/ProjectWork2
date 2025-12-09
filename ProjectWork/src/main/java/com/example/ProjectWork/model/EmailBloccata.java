package com.example.ProjectWork.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "EMAIL_BLOCCATA", schema = "dbo")
public class EmailBloccata {

    @Id
    private String email;

    @Column(nullable = false)
    private LocalDateTime dataEliminazione;

    @Column(nullable = false)
    private boolean haTestNonSuperati = false;

    @Column
    private LocalDateTime dataRiabilitazione;

    //GETTER E SETTER -----------------------------------------------------------------------------------------------

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDataEliminazione() {
        return dataEliminazione;
    }

    public void setDataEliminazione(LocalDateTime dataEliminazione) {
        this.dataEliminazione = dataEliminazione;
    }

    public boolean isHaTestNonSuperati() {
        return haTestNonSuperati;
    }

    public void setHaTestNonSuperati(boolean haTestNonSuperati) {
        this.haTestNonSuperati = haTestNonSuperati;
    }

    public LocalDateTime getDataRiabilitazione() {
        return dataRiabilitazione;
    }

    public void setDataRiabilitazione(LocalDateTime dataRiabilitazione) {
        this.dataRiabilitazione = dataRiabilitazione;
    }
}