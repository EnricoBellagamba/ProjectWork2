package com.example.ProjectWork.dto.test;

import java.util.List;

public class TestCreateRequest {

    public String titolo;
    public String descrizione;
    public Integer durataMinuti = 20;
    public Integer numeroDomande;
    public Integer punteggioMin;

    // codice del tipo test (SOFT_SKILLS, TECNICO, ecc.)
    public String codiceTipoTest;

    public List<DomandaCreateRequest> domande;

    public static class DomandaCreateRequest {
        public String testo;
        // il punteggio per domanda lo useremo dopo, con vincoli 1-10
        public Integer punteggio;
        public List<OpzioneCreateRequest> opzioni;
    }

    public static class OpzioneCreateRequest {
        public String testoOpzione;
        public boolean corretta;
    }
}
