package com.example.spring_shop.mail;

import com.example.spring_shop.domain.User;
import com.example.spring_shop.repository.UserRepository;
import com.example.spring_shop.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    @Scheduled(cron = "0 0 */3 * * *")
    @Transactional
    public void cleanupUnverifiedUsers() {
        LocalDateTime now = LocalDateTime.now();

        List<VerificationToken> expiredTokens = tokenRepository.findAllByExpiryDateBefore(now);

        for (VerificationToken token : expiredTokens) {
            User user = token.getUser();
            if (!user.isEnabled()) {
                tokenRepository.delete(token);
                userRepository.delete(user);
            } else {
                tokenRepository.delete(token);
            }
        }
    }
}