package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.dtos.LoginRequest;
import com.capibara.plaigroundbackend.models.dtos.RegisterRequest;
import com.capibara.plaigroundbackend.services.AuthenticationService;
import com.capibara.plaigroundbackend.services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthenticationService authService;

    private final EmailService emailService;

    @Override
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @Override
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Override
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @Override
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        authService.verifyUser(token);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> newVerifyEmail(@RequestParam String token) {
        emailService.regenerateVerificationToken(token);
        return ResponseEntity.ok().build();
    }
}
