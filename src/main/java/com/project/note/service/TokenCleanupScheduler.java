package com.project.note.service;

import com.project.note.repository.PasswordResetTokenRepository;
import com.project.note.repository.AccountActivationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenCleanupScheduler {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AccountActivationTokenRepository accountActivationTokenRepository;

    @Autowired
    public TokenCleanupScheduler(PasswordResetTokenRepository passwordResetTokenRepository, AccountActivationTokenRepository accountActivationTokenRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.accountActivationTokenRepository = accountActivationTokenRepository;
    }

    @Scheduled(fixedDelay = 3600000)
    public void cleanupExpiredPasswordResetTokens() {
        LocalDateTime now = LocalDateTime.now();
        passwordResetTokenRepository.deleteAllExpiredSince(now);
    }

    @Scheduled(fixedDelay = 86400000)
    public void cleanupExpiredAccountActivationTokens() {
        LocalDateTime now = LocalDateTime.now();
        accountActivationTokenRepository.deleteAllExpiredSince(now);
    }
}
