package com.capibara.plaigroundbackend.services;


import com.capibara.plaigroundbackend.models.VerificationToken;

public interface EmailService {

    void send(String recipient, String nickName, String token);

    VerificationToken createVerificationToken();

    void regenerateVerificationToken(String token);
}
