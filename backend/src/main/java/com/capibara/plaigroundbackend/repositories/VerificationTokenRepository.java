package com.capibara.plaigroundbackend.repositories;

import com.capibara.plaigroundbackend.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByVerificationToken(String token);
}
