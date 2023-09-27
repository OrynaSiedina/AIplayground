package com.capibara.plaigroundbackend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    @Value("${access.token.secret}")
    private String ACCESS_SECRET_KEY;

    @Value("${refresh.token.secret}")
    private String REFRESH_SECRET_KEY;

    @Value("${access.token.expiration}")
    private long accessJwtExpiration;

    @Value("${refresh.token.expiration}")
    private long refreshJwtExpiration;

    @Override
    public String extractUserEmailFromAccessToken(String jwtToken) {
        return extractAccessTokenClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public String extractUserEmailFromRefreshToken(String jwtToken) {
        return extractRefreshTokenClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public UUID extractTokenIdFromRefreshToken(String jwtToken) {
        return UUID.fromString(extractRefreshTokenClaim(jwtToken, claims -> claims.get("TOKEN_ID", String.class)));
    }

    @Override
    public <T> T extractAccessTokenClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllAccessTokenClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public <T> T extractRefreshTokenClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllRefreshTokenClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateAccessToken(Map<String, String> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessJwtExpiration, TokenType.ACCESS_TOKEN);
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        HashMap<String, String> extraClaims = new HashMap<>();
        extraClaims.put("TOKEN_ID", UUID.randomUUID().toString());
        return buildToken(extraClaims, userDetails, refreshJwtExpiration, TokenType.REFRESH_TOKEN);
    }

    private String buildToken(Map<String, String> extraClaims, UserDetails userDetails, long expiration, TokenType tokenType) {
        Key key = null;

        if (tokenType.equals(TokenType.REFRESH_TOKEN)) {
            key = getRefreshTokenSigningKey();
        }

        if (tokenType.equals(TokenType.ACCESS_TOKEN)) {
            key = getAccessTokenSigningKey();
        }
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    private Claims extractAllAccessTokenClaims(String jwtToken) {
        return Jwts.parserBuilder().setSigningKey(getAccessTokenSigningKey()).build().parseClaimsJws(jwtToken).getBody();
    }

    private Claims extractAllRefreshTokenClaims(String jwtToken) {
        return Jwts.parserBuilder().setSigningKey(getRefreshTokenSigningKey()).build().parseClaimsJws(jwtToken).getBody();
    }

    private Key getAccessTokenSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(ACCESS_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshTokenSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean isTokenValid(String userEmail, UserDetails userDetails, Date tokenExpiration) {
        return (userEmail.equals(userDetails.getUsername())) && !tokenExpiration.before(new Date(System.currentTimeMillis()));
    }
}
