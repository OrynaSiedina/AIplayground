package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.models.dtos.AuthenticationResponse;
import com.capibara.plaigroundbackend.models.dtos.LoginRequest;
import com.capibara.plaigroundbackend.models.dtos.RegisterDTO;
import com.capibara.plaigroundbackend.models.dtos.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    RegisterDTO register(RegisterRequest request);

    AuthenticationResponse login(LoginRequest request);

    AuthenticationResponse refreshAccessToken(HttpServletRequest request);

    void verifyUser(String token);
}
