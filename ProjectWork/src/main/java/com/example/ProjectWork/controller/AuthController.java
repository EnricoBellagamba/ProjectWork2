package com.example.ProjectWork.controller;
import com.example.ProjectWork.dto.auth.LoginRequest;
import com.example.ProjectWork.dto.auth.LoginResponse;
import com.example.ProjectWork.dto.auth.RegisterRequest;
import com.example.ProjectWork.exception.EmailGiaRegistrataException;
import com.example.ProjectWork.exception.PasswordErrataException;
import com.example.ProjectWork.exception.RuoloNonValidoException;
import com.example.ProjectWork.exception.UtenteNonTrovatoException;
import com.example.ProjectWork.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> register(
            @RequestPart("payload") RegisterRequest req,
            @RequestPart(value = "cv" , required = false)MultipartFile cvFile
            ) {
        try {
            LoginResponse resp = authService.register(req,cvFile);
            return ResponseEntity.ok(resp);
        } catch (EmailGiaRegistrataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("EMAIL_GIA_REGISTRATA");
        } catch (RuoloNonValidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("RUOLO_NON_VALIDO");
        }  catch (IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "ERRORE_SALVATAGGIO_CV"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse resp = authService.login(req);
            return ResponseEntity.ok(resp);
        } catch (UtenteNonTrovatoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("UTENTE_NON_TROVATO");
        } catch (PasswordErrataException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("PASSWORD_ERRATA");
        }
    }
}