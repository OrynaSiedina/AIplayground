package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.AIPlaygroundException;
import com.capibara.plaigroundbackend.exceptions.DataRequiredException;
import com.capibara.plaigroundbackend.models.Role;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.VerificationToken;
import com.capibara.plaigroundbackend.models.dtos.AuthenticationResponse;
import com.capibara.plaigroundbackend.models.dtos.LoginRequest;
import com.capibara.plaigroundbackend.models.dtos.RegisterDTO;
import com.capibara.plaigroundbackend.models.dtos.RegisterRequest;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuthenticationServiceTest {

    @MockBean
    private JWTService jwtService;

    @MockBean
    private UserEntityRepository userRepo;

    @MockBean
    private EmailService emailService;

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            System.out.println("Sending email to: " + invocation.getArgument(0));
            return null;  // This is fine for a void method.
        }).when(emailService).send(anyString(), anyString(), anyString());
        when(emailService.createVerificationToken()).thenReturn(new VerificationToken());
    }

    @Test
    @DisplayName("Register new user entity from register request and return registerDto")
    void registerNewUserEntityFromRegisterRequestAnReturnRegisterDTO() {
        RegisterRequest request = RegisterRequest.builder()
                .nickname("batman")
                .email("bat@man.com")
                .password("shalala")
                .build();
        UserEntity user = new UserEntity();

        when(userRepo.save(any(UserEntity.class))).thenReturn(user);

        RegisterDTO response = authenticationService.register(request);

        assertNotNull(response);
        assertNotNull(response.getNickname());
        assertEquals("batman", response.getNickname());
        assertEquals("bat@man.com", response.getEmail());
        Mockito.verify(userRepo, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Edge case: register method should throw new DataRequiredException when no data are provided")
    void registerWithMissingDataShouldThrowDataRequiredException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("")
                .password("")
                .build();

        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            authenticationService.register(request);
        });
        assertEquals("You are missing required field(s): Password and Email", exception.getMessage());
    }

    @Test
    @DisplayName("Edge case: register method should throw new DataRequiredException when no email is provided")
    void registerWithMissingEmailShouldThrowDataRequiredException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("")
                .password("password")
                .build();

        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            authenticationService.register(request);
        });
        assertEquals("You are missing required field(s): Email", exception.getMessage());
    }

    @Test
    @DisplayName("Edge case: register method should throw new DataRequiredException when no password is provided")
    void registerWithMissingPasswordShouldThrowDataRequiredException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("JohnDoe@gmail.com")
                .password("")
                .build();

        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            authenticationService.register(request);
        });
        assertEquals("You are missing required field(s): Password", exception.getMessage());
    }

    @Test
    @DisplayName("Edge case: register method should throw new AIPlaygroundException when email is already used")
    void registerWithAlreadyUsedEmailShouldThrowException() {
        UserEntity usedUser = UserEntity.builder()
                .nickname("batman")
                .email("Batman@batman.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();

        when(userRepo.findByEmail(any(String.class))).thenReturn(java.util.Optional.of(usedUser));

        RegisterRequest request = RegisterRequest.builder()
                .nickname("batman")
                .email("batman@batman.com")
                .password("password")
                .build();

        AIPlaygroundException exception = Assertions.assertThrows(AIPlaygroundException.class, () -> {
            authenticationService.register(request);
        });
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Login user from login request")
    void loginUserFromLoginRequest() {
        LoginRequest request = LoginRequest.builder()
                .email("bat@man.com")
                .password("password")
                .build();

        UserEntity user = UserEntity.builder()
                .nickname("batman")
                .email("batman@man.com")
                .password(passwordEncoder.encode("password"))
                .isEnabled(true)
                .role(Role.USER)
                .build();

        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(user);
        when(userRepo.findByEmail(any(String.class))).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateAccessToken(any(UserEntity.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(UserEntity.class))).thenReturn("refreshToken");

        AuthenticationResponse response = authenticationService.login(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        Mockito.verify(userRepo, times(1)).findByEmail(any(String.class));
        Mockito.verify(jwtService, times(1)).generateAccessToken(any(UserEntity.class));
        Mockito.verify(jwtService, times(1)).generateRefreshToken(any(UserEntity.class));
    }

    @Test
    @DisplayName("Edge case: login method should throw new DataRequiredException when no data are provided")
    void loginWithMissingDataShouldThrowAIPlaygroundException() {
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("")
                .build();

        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            authenticationService.login(request);
        });
        assertEquals("You are missing required field(s): Password and Email", exception.getMessage());
    }

    @Test
    @DisplayName("Edge case: login method should throw new DataRequiredException when no email is provided")
    void loginWithMissingEmailShouldThrowAIPlaygroundException() {
        LoginRequest request = LoginRequest.builder()
                .email("")
                .password("password")
                .build();

        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            authenticationService.login(request);
        });
        assertEquals("You are missing required field(s): Email", exception.getMessage());
    }

    @Test
    @DisplayName("Edge case: login method should throw new DataRequiredException when no password is provided")
    void loginWithMissingPasswordShouldThrowInvalidDataException() {
        LoginRequest request = LoginRequest.builder()
                .email("JohnDoe@gmail.com")
                .password("")
                .build();
        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            authenticationService.login(request);
        });
        assertEquals("You are missing required field(s): Password", exception.getMessage());
    }

    @Test
    @DisplayName("Login method should throw new InvalidDataException when incorrect password or email is provided")
    void loginWithWrongPasswordShouldThrowInvalidDataException() {
        LoginRequest request = LoginRequest.builder()
                .email("bat@man.com")
                .password("passord")
                .build();

        UserEntity user = UserEntity.builder()
                .nickname("batman")
                .email("batman@man.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .isEnabled(true)
                .build();

        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(user);
        when(userRepo.findByEmail(any(String.class))).thenReturn(java.util.Optional.of(user));

        AIPlaygroundException exception = Assertions.assertThrows(AIPlaygroundException.class, () -> {
            authenticationService.login(request);
        });

        assertEquals("Username or Password is invalid!", exception.getMessage());
    }
    @Test
    @DisplayName("Login method should throw new AiPlaigroudException when incorrect password or email is provided")
    void loginWithDisabledAccountShouldThrowAiPlaygroundException() {
        LoginRequest request = LoginRequest.builder()
                .email("bat@man.com")
                .password("passord")
                .build();

        UserEntity user = UserEntity.builder()
                .nickname("batman")
                .email("batman@man.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .isEnabled(false)
                .build();

        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(user);
        when(userRepo.findByEmail(any(String.class))).thenReturn(java.util.Optional.of(user));

        AIPlaygroundException exception = Assertions.assertThrows(AIPlaygroundException.class, () -> {
            authenticationService.login(request);
        });

        assertEquals("Account is disabled, check your email for verification instructions", exception.getMessage());
    }

    @Test
    @DisplayName("Refresh token should return new access token")
    void refreshTokenShouldReturnNewAccessToken() {
        String token = "refresh_token";
        String email = "JohnBatman@gmail.com";
        UserEntity user = new UserEntity();
        user.setRefreshTokenUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        when(jwtService.extractTokenIdFromRefreshToken(token)).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.extractUserEmailFromRefreshToken(token)).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new_access_token");

        AuthenticationResponse response = authenticationService.refreshAccessToken(request);
        assertEquals("new_access_token", response.getAccessToken());
        Mockito.verify(jwtService, times(1)).extractUserEmailFromRefreshToken(token);
        Mockito.verify(userRepo, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Edge case: refresh token should throw new AIPlaygroundException when wrong token UUID is provided")
    void refreshTokenShouldNotReturnNewAccessToken() {
        String token = "refresh_token";
        String email = "JohnBatman@gmail.com";
        UserEntity user = new UserEntity();
        user.setRefreshTokenUUID(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtService.extractUserEmailFromRefreshToken(token)).thenReturn(email);
        when(userRepo.findByEmail(email)).thenReturn(java.util.Optional.of(user));
        when(jwtService.extractTokenIdFromRefreshToken(token)).thenReturn(UUID.fromString("00000000-0000-0000-0000-000000000000"));

        AIPlaygroundException exception = Assertions.assertThrows(AIPlaygroundException.class, () -> {
            authenticationService.refreshAccessToken(request);
        });

        assertEquals("Unauthorized, please login again!", exception.getMessage());
    }

    @Test
    @DisplayName("Refresh token should throw unauthorized exception when no refresh token provided")
    void refreshTokenShouldNotReturnNewAccessTokenWhenNoRefreshTokenProvided() {
        UserEntity user = new UserEntity();
        user.setRefreshTokenUUID(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        AIPlaygroundException exception = Assertions.assertThrows(AIPlaygroundException.class, () -> {
            authenticationService.refreshAccessToken(request);
        });

        assertEquals("Unauthorized, please login again!", exception.getMessage());
    }
}
