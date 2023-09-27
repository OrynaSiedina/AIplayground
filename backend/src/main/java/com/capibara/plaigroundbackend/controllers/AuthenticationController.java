package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.dtos.LoginRequest;
import com.capibara.plaigroundbackend.models.dtos.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@Tag(name = "Authentication controller", description = "API to handle registration, login and refresh requests")
public interface AuthenticationController {
    @PostMapping("/register")
    @Operation(summary = "Post request to register new user",
            description = "Enter your email and password to register a new user. " +
                    "If the email is already taken, you will get an error message.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "User with this email already exists")
    })
    ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest);

    @PostMapping("/login")
    @Operation(summary = "Post request to log in user",
            description = "By entering user email and password, our database will log in the user, while providing him access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged in"),
            @ApiResponse(responseCode = "400", description = "User with this email does not exist or password is incorrect")
    })
    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest);

    @GetMapping("/refresh")
    @Operation(summary = "Get request to refresh access token",
            description = "Provide the refresh token by clicking on the lock icon and execute to get a new JWT access token.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token successfully refreshed"),
            @ApiResponse(responseCode = "401", description = "Refresh token is invalid or expired")
    })
    ResponseEntity<?> refreshToken(HttpServletRequest request);

    @GetMapping("/verify")
    @Operation(summary = "Get request to verify email",
            description = "Provide the verification token by clicking on the lock icon and execute to verify your email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email successfully verified"),
            @ApiResponse(responseCode = "401", description = "Verification token is invalid or expired")
    })
    ResponseEntity<?> verifyEmail(@RequestParam String token);

    @PatchMapping("/verify")
    @Operation(summary = "Get request to send new verification email",
            description = "Provide the email by clicking on the lock icon and execute to send a new verification email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New verification email successfully sent"),
            @ApiResponse(responseCode = "401", description = "Email is invalid or already verified"),
            @ApiResponse(responseCode = "404", description = "User with this email does not exist")
    })
    ResponseEntity<?> newVerifyEmail(@RequestParam String token);
}