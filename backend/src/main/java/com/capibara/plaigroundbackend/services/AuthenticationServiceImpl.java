package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.*;
import com.capibara.plaigroundbackend.models.Role;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.VerificationToken;
import com.capibara.plaigroundbackend.models.dtos.AuthenticationResponse;
import com.capibara.plaigroundbackend.models.dtos.LoginRequest;
import com.capibara.plaigroundbackend.models.dtos.RegisterDTO;
import com.capibara.plaigroundbackend.models.dtos.RegisterRequest;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import com.capibara.plaigroundbackend.repositories.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserEntityRepository userRepo;
    private final AuthenticationManager authManager;
    private final EmailService emailService;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public RegisterDTO register(RegisterRequest request) {

        Optional<UserEntity> optionalUser = userRepo.findByEmail(request.getEmail());

        if (optionalUser.isPresent() && optionalUser.get().getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        validateRequest(request.getPassword(), request.getEmail());

        UserEntity user = UserEntity
                .builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .verificationToken(emailService.createVerificationToken())
                .isEnabled(false)
                .build();

        emailService.send(user.getEmail(), user.getNickname(), user.getVerificationToken().getVerificationToken());

        userRepo.save(user);

        return RegisterDTO
                .builder()
                .nickname(user.getNickname())
                .email(user.getUsername())
                .build();
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {

        validateRequest(request.getPassword(), request.getEmail());

        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (DisabledException e) {
            throw new AIPlaygroundException("Account is disabled, check your email for verification instructions", HttpStatusCode.valueOf(403));
        } catch (Exception e) {
            throw new InvalidDataException("Username or Password");
        }

        var user = userRepo.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setRefreshTokenUUID(jwtService.extractTokenIdFromRefreshToken(refreshToken));
        userRepo.save(user);
        return AuthenticationResponse
                .builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void validateRequest(String password, String email) {
        if (password.isEmpty() && email.isEmpty()) {
            throw new DataRequiredException("Password and Email");
        }

        if (password.isEmpty()) {
            throw new DataRequiredException("Password");
        }

        if (email.isEmpty()) {
            throw new DataRequiredException("Email");
        }
    }

    @Override
    public AuthenticationResponse refreshAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorisedException();
        }
        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUserEmailFromRefreshToken(refreshToken);

        UserEntity user = userRepo.findByEmail(userEmail).orElseThrow();
        if (jwtService.extractTokenIdFromRefreshToken(refreshToken).equals(user.getRefreshTokenUUID())) {
            String newAccessToken = jwtService.generateAccessToken(user);
            return AuthenticationResponse.builder().accessToken(newAccessToken).build();
        } else {
            throw new UnauthorisedException();
        }
    }

    @Override
    public void verifyUser(String token) {
        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByVerificationToken(token);

        if (optionalVerificationToken.isEmpty()) {
            throw new InvalidDataException("Token");
        }

        VerificationToken verificationToken = optionalVerificationToken.get();
        if (verificationToken.getVerificationTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidDataException("Token");
        }

        UserEntity user = verificationToken.getUser();
        if (user == null) {
            throw new NotFoundException("User");
        }
        if (user.isEnabled()) {
            throw new AlreadyInListException("User is already verified", HttpStatusCode.valueOf(400));
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepo.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
}
