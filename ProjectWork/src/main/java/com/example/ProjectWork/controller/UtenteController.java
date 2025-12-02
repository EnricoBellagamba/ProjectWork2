package com.example.ProjectWork.controller;
import com.example.ProjectWork.dto.utente.UpdatePasswordRequest;
import com.example.ProjectWork.dto.utente.UpdateProfiloCandidatoRequest;
import com.example.ProjectWork.dto.utente.UtenteDto;
import com.example.ProjectWork.model.EmailBloccata;
import com.example.ProjectWork.model.Utente;
import com.example.ProjectWork.repository.EmailBloccataRepository;
import com.example.ProjectWork.repository.UtenteRepository;
import com.example.ProjectWork.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailBloccataRepository emailBloccataRepository;


    // GET ALL
    @GetMapping
    public ResponseEntity<List<Utente>> getAllUtenti() {
        return ResponseEntity.ok(utenteService.getAllUtenti());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Utente> getUtenteById(@PathVariable Long id) {
        return ResponseEntity.ok(utenteService.getUtenteById(id));
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Utente> createUtente(@Valid @RequestBody Utente utente) {
        return ResponseEntity.ok(utenteService.createUtente(utente));
    }

    // UPDATE
    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
        )
    public ResponseEntity<UtenteDto> updateUtente(
            @PathVariable Long id,
            @Valid @RequestPart("payload") UpdateProfiloCandidatoRequest req,
            @RequestPart(value = "cv", required = false) MultipartFile cvFile) throws IOException {

        UtenteDto updated = utenteService.updateUtente(id, req, cvFile);
        return ResponseEntity.ok(updated);
    }

    // CAMBIO PASSWORD
    @PostMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordRequest request
    ) {
        utenteService.updatePassword(id, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtente(@PathVariable Long id) {

        Utente utenteDaCancellare = utenteService.getUtenteById(id);
        String emailDaBloccare = utenteDaCancellare.getEmail();

        LocalDateTime dataEliminazione = LocalDateTime.now();
        LocalDateTime dataRiabilitazione = dataEliminazione.plusMonths(1); // Un mese dopo l'eliminazione


        EmailBloccata emailBloccata = new EmailBloccata();

        emailBloccata.setEmail(emailDaBloccare);
        emailBloccata.setDataEliminazione(dataEliminazione);
        emailBloccata.setDataRiabilitazione(dataRiabilitazione);

        emailBloccataRepository.save(emailBloccata);

        utenteService.deleteUtente(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{idUtente}/preferiti/{idPosizione}")
    public ResponseEntity<?> aggiungiPosizionePreferita(
            @PathVariable Long idUtente,
            @PathVariable Long idPosizione) {

        utenteService.aggiungiPosizionePreferita(idUtente, idPosizione);
        return ResponseEntity.ok().build();
    }

}

