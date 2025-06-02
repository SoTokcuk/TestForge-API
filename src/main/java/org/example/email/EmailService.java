package org.example.email;

public interface EmailService {

    void sendSimpleEmail(final String toAddress, final String subject, final String message);
    void sendVerificationEmail(final String to, final String token);
}