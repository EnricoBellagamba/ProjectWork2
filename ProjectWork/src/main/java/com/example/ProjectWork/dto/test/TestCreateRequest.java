package com.example.ProjectWork.dto.test;

import java.util.List;

public class TestCreateRequest {

    public String titolo;
    public String descrizione;
    public Integer durataMinuti = 20;
    public Integer numeroDomande;
    public Integer punteggioMin;
    public String codiceTipoTest;
    public List<DomandaCreateRequest> domande;

    public static class DomandaCreateRequest {
        public String testo;
        public Integer punteggio;
        public List<OpzioneCreateRequest> opzioni;
    }

    public static class OpzioneCreateRequest {
        public String testoOpzione;
        public boolean corretta;
    }
}
