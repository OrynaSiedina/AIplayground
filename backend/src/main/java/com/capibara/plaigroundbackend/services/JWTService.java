package com.capibara.plaigroundbackend.services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public interface JWTService {
    String extractUserEmailFromAccessToken(String jwtToken);

    String extractUserEmailFromRefreshToken(String jwtToken);

    UUID extractTokenIdFromRefreshToken(String jwtToken);

    <T> T extractAccessTokenClaim(String token, Function<Claims, T> claimsResolver);

    <T> T extractRefreshTokenClaim(String token, Function<Claims, T> claimsResolver);

    String generateAccessToken(Map<String, String> extraClaims, UserDetails userDetails);

    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    boolean isTokenValid(String userEmail, UserDetails userDetails, Date tokenExpiration);
}
