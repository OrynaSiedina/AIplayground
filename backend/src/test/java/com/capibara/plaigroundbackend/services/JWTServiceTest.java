package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.models.Role;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JWTServiceTest {

    @Autowired
    private JWTService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserEntityRepository userRepo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Extracts user email from refresh token")
    void extractUserEmailFromRefreshToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getRefreshTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getRefreshTokenSigningKey");
        getRefreshTokenSigningKey.setAccessible(true);

        String expectedEmail = "batman@call.me";
        String expectedToken = Jwts.builder()
                .setClaims(new HashMap<>()).setSubject("batman@call.me")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getRefreshTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();

        String extractedEmail = jwtService.extractUserEmailFromRefreshToken(expectedToken);

        assertEquals(expectedEmail, extractedEmail);
    }

    @Test
    @DisplayName("Extracts user email from access token")
    void extractUserEmailFromAccessToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getAccessTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getAccessTokenSigningKey");
        getAccessTokenSigningKey.setAccessible(true);

        String expectedEmail = "batman@call.me";
        String expectedToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject("batman@call.me")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getAccessTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();

        String extractedEmail = jwtService.extractUserEmailFromAccessToken(expectedToken);

        assertEquals(expectedEmail, extractedEmail);
    }

    @Test
    @DisplayName("Extracts Token ID from refresh token")
    void extractTokenIdFromRefreshToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getRefreshTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getRefreshTokenSigningKey");
        getRefreshTokenSigningKey.setAccessible(true);

        HashMap<String, String> extraClaims = new HashMap<>();
        extraClaims.put("TOKEN_ID", "00000000-0000-0000-0000-000000000000");

        String testToken = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject("batman@call.me")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getRefreshTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();

        UUID extractedTokenId = jwtService.extractTokenIdFromRefreshToken(testToken);
        UUID expectedTokenId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        assertEquals(expectedTokenId, extractedTokenId);
    }

    @Test
    @DisplayName("Extracts claim from access token")
    void extractAccessTokenClaim() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getAccessTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getAccessTokenSigningKey");
        getAccessTokenSigningKey.setAccessible(true);

        String expectedEmail = "batman@call.me";
        String testToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject("batman@call.me")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getAccessTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();
        String expectedClaim = jwtService.extractAccessTokenClaim(testToken, Claims::getSubject);
        assertEquals(expectedEmail, expectedClaim);
    }

    @Test
    @DisplayName("Extracts claim from refresh token")
    void extractRefreshTokenClaim() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getRefreshTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getRefreshTokenSigningKey");
        getRefreshTokenSigningKey.setAccessible(true);

        String expectedEmail = "batman@call.me";
        String testToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject("batman@call.me")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getRefreshTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();
        String expectedClaim = jwtService.extractRefreshTokenClaim(testToken, Claims::getSubject);
        assertEquals(expectedEmail, expectedClaim);
    }

    @Test
    @DisplayName("Generates access token")
    void generateAccessToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getAccessTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getAccessTokenSigningKey");
        getAccessTokenSigningKey.setAccessible(true);

        UserEntity user = UserEntity.builder()
                .nickname("testUsername")
                .email("testEmail")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER).build();

        String testToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getAccessTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();

        String generatedToken = jwtService.generateAccessToken(user);

        assertEquals(jwtService.extractUserEmailFromAccessToken(testToken), jwtService.extractUserEmailFromAccessToken(generatedToken));
    }

    @Test
    @DisplayName("Generates refresh token")
    void generateRefreshToken() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getRefreshTokenSigningKey = JWTServiceImpl.class.getDeclaredMethod("getRefreshTokenSigningKey");
        getRefreshTokenSigningKey.setAccessible(true);
        UserEntity user = UserEntity.builder().nickname("Batman").email("wtfemail").password(passwordEncoder.encode("123456")).role(Role.USER).build();

        String testToken = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 500000))
                .signWith((Key) getRefreshTokenSigningKey.invoke(jwtService), SignatureAlgorithm.HS256).compact();

        String generatedToken = jwtService.generateRefreshToken(user);

        assertEquals(jwtService.extractUserEmailFromRefreshToken(testToken), jwtService.extractUserEmailFromRefreshToken(generatedToken));
    }

    @Test
    @DisplayName("Returns true if token is valid")
    void isTokenValid() {
        UserEntity user = UserEntity.builder()
                .nickname("testUsername")
                .email("testEmail")
                .password(passwordEncoder.encode("123456"))
                .role(Role.USER).build();

        UserEntity savedUser = userRepo.save(user);
        String generatedToken = jwtService.generateAccessToken(savedUser);
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        Date tokenExpiration = jwtService.extractAccessTokenClaim(generatedToken, Claims::getExpiration);

        boolean isTokenValid = jwtService.isTokenValid(user.getEmail(), userDetails, tokenExpiration);

        assertTrue(isTokenValid);
    }
}