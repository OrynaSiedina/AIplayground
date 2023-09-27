package com.capibara.plaigroundbackend.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {
    @Id
    @GeneratedValue
    private Long id;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiration;

    @OneToOne(mappedBy = "verificationToken")
    private UserEntity user;
}
