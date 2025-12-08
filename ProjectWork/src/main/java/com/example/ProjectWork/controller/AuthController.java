package com.example.ProjectWork.controller;

import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import com.example.ProjectWork.exception.*;
import com.example.ProjectWork.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> register(
            @Valid @RequestPart("payload") RegisterRequest req,
            @RequestPart(value = "cv", required = false) MultipartFile cvFile
    ) {
        try {
            LoginResponse resp = authService.register(req, cvFile);
            return ResponseEntity.ok(resp);
        } catch (EmailGiaRegistrataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("EMAIL_GIA_REGISTRATA");
        } catch (RuoloNonValidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RUOLO_NON_VALIDO");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "ERRORE_SALVATAGGIO_CV"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            LoginResponse resp = authService.login(req);
            return ResponseEntity.ok(resp);
        } catch (UtenteNonTrovatoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("UTENTE_NON_TROVATO");
        } catch (PasswordErrataException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "CREDENZIALI_NON_VALIDE"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> req) {
        String refreshToken = req.get("refreshToken");

        try {
            LoginResponse resp = authService.refresh(refreshToken);
            return ResponseEntity.ok(resp);
        } catch (TokenNonValidoException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "REFRESH_TOKEN_INVALIDO"));
        }
    }

}
