package org.example.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class DefaultEmailService implements EmailService {

    @Autowired
    public JavaMailSender emailSender;

    @Override
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        emailSender.send(simpleMailMessage);
    }

//    @Override
//    public void sendVerificationEmail(String to, String token) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Подтверждение email");
//        message.setText("Для подтверждения email перейдите по ссылке: http://localhost:8080/verify-email?token=" + token);
//        emailSender.send(message);
//    }

    @Override
    public void sendVerificationEmail(String to, String token) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            String verificationUrl = "http://localhost:8080/api/reg/verify-email?token=" + token;

            String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 15px;
                        text-align: center;
                        border-radius: 5px 5px 0 0;
                    }
                    .content {
                        padding: 20px;
                        background-color: #f9f9f9;
                        border-radius: 0 0 5px 5px;
                    }
                    .button {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #4CAF50;
                        color: white !important;
                        text-decoration: none;
                        border-radius: 5px;
                        margin: 15px 0;
                    }
                    .footer {
                        margin-top: 20px;
                        font-size: 12px;
                        color: #777;
                        text-align: center;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <h2>Подтверждение email</h2>
                </div>
                <div class="content">
                    <p>Здравствуйте!</p>
                    <p>Благодарим вас за регистрацию. Для завершения процесса регистрации, пожалуйста, подтвердите ваш email, нажав на кнопку ниже:</p>
                    
                    <p style="text-align: center;">
                        <a href="%s" class="button">Подтвердить Email</a>
                    </p>
                    
                    <p>Если кнопка не работает, скопируйте и вставьте следующую ссылку в ваш браузер:</p>
                    <p><a href="%s">%s</a></p>
                    
                    <p>Если вы не регистрировались на нашем сайте, пожалуйста, проигнорируйте это письмо.</p>
                </div>
                <div class="footer">
                    <p>© 2025 DreamTeam&Co. Все права защищены.</p>
                </div>
            </body>
            </html>
            """.formatted(verificationUrl, verificationUrl, verificationUrl);
            //helper.setFrom("dreamteam.corparation@gmail.com", "Биба и боба");
            helper.setFrom("awf32ds@gmail.com", "Arly");
            helper.setTo(to);
            helper.setSubject("Подтверждение email");
            helper.setText(htmlContent, true); // true indicates HTML

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}