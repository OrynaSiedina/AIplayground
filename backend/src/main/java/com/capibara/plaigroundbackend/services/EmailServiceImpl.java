package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.NotFoundException;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.VerificationToken;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import com.capibara.plaigroundbackend.repositories.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserEntityRepository userEntityRepository;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${frontEnd.url}")
    private String link;

    @Override
    public void send(String recipient, String nickName, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(recipient);
        mailMessage.setSubject("PlAIground Email Verification");
        mailMessage.setText("Hello " + nickName + "\n" +
                "Please verify your email by clicking the link below:\n" +
                link + "/verification/" + token + "\n" +

                "This link will expire in 15 minutes.");
        javaMailSender.send(mailMessage);
    }

    @Override
    public VerificationToken createVerificationToken() {
        VerificationToken verificationToken = VerificationToken.builder()
                .verificationToken(UUID.randomUUID().toString())
                .verificationTokenExpiration(LocalDateTime.now().plusMinutes(15))
                .build();
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public void regenerateVerificationToken(String originalToken) {
        Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByVerificationToken(originalToken);
        if (optionalVerificationToken.isEmpty()) {
            throw new NotFoundException("Token");
        }
        VerificationToken oldToken = optionalVerificationToken.get();

        UserEntity user = oldToken.getUser();
        if (user == null) {
            throw new NotFoundException("User");
        }

        VerificationToken newToken = createVerificationToken();

        user.setVerificationToken(newToken);
        userEntityRepository.save(user);

        verificationTokenRepository.delete(oldToken);

        send(user.getEmail(), user.getNickname(), newToken.getVerificationToken());
    }
}
