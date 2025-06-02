package org.example.email;

import org.example.email.exceptions.TokenExpiredException;
import org.example.email.exceptions.TokenNotFoundException;
import org.example.entity.EmailVerificationToken;
import org.example.entity.MyUser;
import org.example.repository.EmailVerificationTokenRepository;
import org.example.service.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class EmailVerificationService {
    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private MyUserDetailService userService;

    @Autowired
    private EmailService emailService;

    public String generateVerificationToken(MyUser user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, expiresAt, user);
        tokenRepository.save(verificationToken);
        return token;
    }

    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Неверный токен подтверждения"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Срок действия токена подтверждения истек");
        }

        MyUser user = verificationToken.getUser();
        user.setEmailVerified(true);
        tokenRepository.delete(verificationToken);
    }

    public void deleteExistingTokens (MyUser user) {
        tokenRepository.deleteAllByUserId(user.getId());
    }
}
