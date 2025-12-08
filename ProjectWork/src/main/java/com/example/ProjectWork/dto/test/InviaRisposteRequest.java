package com.example.ProjectWork.dto.test;

import java.util.List;

public class InviaRisposteRequest {
    private Long idTentativo;
    private List<RispostaInput> risposte;

    public InviaRisposteRequest() {
    }

    public Long getIdTentativo() {
        return idTentativo;
    }

    public void setIdTentativo(Long idTentativo) {
        this.idTentativo = idTentativo;
    }

    public List<RispostaInput> getRisposte() {
        return risposte;
    }

    public void setRisposte(List<RispostaInput> risposte) {
        this.risposte = risposte;
    }

    public static class RispostaInput {
        private Long idDomanda;
        private Long idOpzione;

        public RispostaInput() {
        }

        public Long getIdDomanda() {
            return idDomanda;
        }

        public void setIdDomanda(Long idDomanda) {
            this.idDomanda = idDomanda;
        }

        public Long getIdOpzione() {
            return idOpzione;
        }

        public void setIdOpzione(Long idOpzione) {
            this.idOpzione = idOpzione;
        }
    }
}
