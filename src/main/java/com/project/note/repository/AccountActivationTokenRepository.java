package com.project.note.repository;

import com.project.note.model.AccountActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Long> {
    Optional<AccountActivationToken> findByToken(String token);
    @Transactional
    @Modifying
    @Query("DELETE FROM AccountActivationToken t WHERE t.expiryDate <= ?1")
    void deleteAllExpiredSince(LocalDateTime now);
}
