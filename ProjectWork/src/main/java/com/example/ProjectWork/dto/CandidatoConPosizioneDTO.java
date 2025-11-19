package com.example.ProjectWork.dto;

public record CandidatoConPosizioneDTO(
        Long idCandidato,
        Long idUtente,
        Boolean isActive,
        String nome,
        String cognome,
        String email,
        String telefono,
        String lastLogin,
        String citta,
        String lingua,
        String cvUrl,
        String posizioneTitolo
) {}
