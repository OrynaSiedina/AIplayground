package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.PlAIgroundBackendApplication;
import com.capibara.plaigroundbackend.models.Role;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.VerificationToken;
import com.capibara.plaigroundbackend.models.dtos.AuthenticationResponse;
import com.capibara.plaigroundbackend.models.dtos.LoginRequest;
import com.capibara.plaigroundbackend.models.dtos.RegisterDTO;
import com.capibara.plaigroundbackend.models.dtos.RegisterRequest;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import com.capibara.plaigroundbackend.services.EmailService;
import com.capibara.plaigroundbackend.services.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PlAIgroundBackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerIntTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserEntityRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
        doAnswer(invocation -> {
            System.out.println("Sending email to: " + invocation.getArgument(0));
            return null;  // This is fine for a void method.
        }).when(emailService).send(anyString(), anyString(), anyString());
        when(emailService.createVerificationToken()).thenReturn(new VerificationToken());
    }

    @Test
    void shouldRegisterNewUserEntityFromRegisterRequest() {
        HttpHeaders headers = new HttpHeaders();

        RegisterRequest request = new RegisterRequest();
        request.setNickname("Batman");
        request.setEmail("wtf@wtf.wtf");
        request.setPassword("abcddacd5454");

        RegisterDTO registerDTO = RegisterDTO.builder().nickname("Batman").email("wtf@wtf.wtf").build();

        HttpEntity<RegisterRequest> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<RegisterDTO> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/register", port),
                        HttpMethod.POST,
                        httpEntity,
                        RegisterDTO.class);


        assertEquals(200, response.getStatusCode().value(), response.toString());
        assertEquals(registerDTO.getEmail(), response.getBody().getEmail());
        assertEquals(registerDTO.getNickname(), response.getBody().getNickname());
        assertEquals(1, userRepo.count());
    }

    @Test
    void shouldLoginRegisteredUser() {
        UserEntity user = UserEntity.builder()
                .nickname("JohnDoe")
                .email("JohnDoe@gmail.com")
                .password(passwordEncoder.encode("12345"))
                .role(Role.USER)
                .isEnabled(true)
                .appList(new ArrayList<>())
                .build();

        UserEntity savedUser = userRepo.save(user);

        HttpHeaders headers = new HttpHeaders();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(savedUser.getEmail());
        loginRequest.setPassword("12345");

        HttpEntity<LoginRequest> httpEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<AuthenticationResponse> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST,
                        httpEntity,
                        AuthenticationResponse.class);

        assertEquals(200, response.getStatusCode().value(), response.toString());
        assertNotNull(response.getBody().getAccessToken(), response.getBody().getRefreshToken());
    }

    @Test
    void shouldReturnNewRefreshToken() {
        UserEntity user = UserEntity.builder()
                .nickname("JohnDoe")
                .email("JohnDoe@gmail.com")
                .password(passwordEncoder.encode("12345"))
                .role(Role.USER)
                .appList(new ArrayList<>())
                .build();

        UserEntity savedUser = userRepo.save(user);
        String refreshToken = jwtService.generateRefreshToken(savedUser);
        user.setRefreshTokenUUID(jwtService.extractTokenIdFromRefreshToken(refreshToken));
        userRepo.save(user);


        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + refreshToken);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<AuthenticationResponse> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/refresh", port),
                        HttpMethod.GET,
                        httpEntity,
                        AuthenticationResponse.class);

        assertEquals(200, response.getStatusCode().value(), response.toString());
        assertNotNull(response.getBody().getAccessToken());
        assertNull(response.getBody().getRefreshToken());
    }

}
