package org.example.controller;

import org.example.email.DefaultEmailService;
import org.example.email.EmailVerificationService;
import org.example.email.exceptions.TokenExpiredException;
import org.example.email.exceptions.TokenNotFoundException;
import org.example.email.exceptions.UserNotFoundException;
import org.example.entity.MyUser;
import org.example.service.MyUserDetailService;
import org.example.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/reg")
public class RegistrationController {

    /*@Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register/user")
    public MyUser createUser(@RequestBody MyUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return myUserRepository.save(user);
    }*/

    @Autowired
    private MyUserDetailService userDetailService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private DefaultEmailService defaultEmailService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        try {
            MyUser user = userDetailService.registerUser(username, email, password);
            String token = emailVerificationService.generateVerificationToken(user);
            defaultEmailService.sendVerificationEmail(user.getEmail(), token);

            return ResponseEntity.ok(
                    Map.of("message", "Регистрация успешна. Проверьте email для подтверждения.")
            );

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Произошла ошибка при регистрации")
            );
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            emailVerificationService.verifyEmail(token);

            String successHtml = buildSuccessHtml();
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(successHtml);

        } catch (TokenNotFoundException e) {
            String errorHtml = buildErrorHtml("Неверный токен подтверждения",
                    "Пожалуйста, запросите новую ссылку для подтверждения");
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_HTML).body(errorHtml);

        } catch (TokenExpiredException e) {
            String errorHtml = buildErrorHtml("Срок действия токена истек",
                    "Пожалуйста, запросите новую ссылку для подтверждения");
            return ResponseEntity.status(HttpStatus.GONE)
                    .contentType(MediaType.TEXT_HTML)
                    .body(errorHtml);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, Object>> resendVerification(
            @RequestParam @Email @NotBlank String email) {

        try {
            MyUser user = userService.loadUserByEmail(email);

            if (user.isEmailVerified()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "status", "error",
                                "code", "EMAIL_ALREADY_VERIFIED",
                                "message", "Email уже подтвержден",
                                "timestamp", LocalDateTime.now()
                        ));
            }

            emailVerificationService.deleteExistingTokens(user);

            String token = emailVerificationService.generateVerificationToken(user);
            defaultEmailService.sendVerificationEmail(email, token);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "status", "success",
                            "message", "Письмо с подтверждением отправлено",
                            "email", email,
                            "timestamp", LocalDateTime.now()
                    ));

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "error",
                            "code", "USER_NOT_FOUND",
                            "message", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        } catch (MailException e) {
            //log.error("Ошибка отправки email: " + email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "code", "EMAIL_SENDING_FAILED",
                            "message", "Не удалось отправить письмо с подтверждением",
                            "timestamp", LocalDateTime.now()
                    ));
        } catch (Exception e) {
            //log.error("Неожиданная ошибка: " + email, e);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "error",
                            "code", "INTERNAL_ERROR",
                            "message", "Внутренняя ошибка сервера",
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }


    private String buildSuccessHtml() {
            String htmlResponse = """
            <!DOCTYPE html>
            <html lang="ru">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Email подтверждён</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    }
                    body {
                        background-color: #f5f7fa;
                        display: flex;
                        justify-content: center;
                        align-items: center;
                        min-height: 100vh;
                        padding: 20px;
                    }
                    .container {
                        max-width: 600px;
                        width: 100%;
                        background: white;
                        border-radius: 12px;
                        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                        overflow: hidden;
                        text-align: center;
                    }
                    .header {
                        background: linear-gradient(135deg, #4f46e5, #7c3aed);
                        color: white;
                        padding: 30px 20px;
                    }
                    .content {
                        padding: 40px 30px;
                    }
                    h1 {
                        font-size: 28px;
                        margin-bottom: 20px;
                        color: #1e293b;
                    }
                    p {
                        font-size: 16px;
                        color: #64748b;
                        line-height: 1.6;
                        margin-bottom: 30px;
                    }
                    .icon {
                        font-size: 80px;
                        color: #10b981;
                        margin-bottom: 20px;
                    }
                    .button {
                        display: inline-block;
                        background: #4f46e5;
                        color: white;
                        text-decoration: none;
                        padding: 12px 24px;
                        border-radius: 6px;
                        font-weight: 500;
                        transition: all 0.3s ease;
                        margin-top: 20px;
                    }
                    .button:hover {
                        background: #4338ca;
                        transform: translateY(-2px);
                        box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
                    }
                    .footer {
                        margin-top: 30px;
                        font-size: 14px;
                        color: #94a3b8;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Поздравляем!</h1>
                    </div>
                    <div class="content">
                        <div class="icon">✓</div>
                        <h1>Ваш email успешно подтверждён</h1>
                        <p>Благодарим вас за подтверждение email-адреса. Теперь вы можете пользоваться всеми возможностями нашего сервиса.</p>
                        <a href="/login" class="button">Перейти к входу</a>
                        <div class="footer">
                            <p>Если у вас возникли вопросы, пожалуйста, свяжитесь с нашей службой поддержки.</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
            """;
            return htmlResponse;
        }

        private String buildErrorHtml(String title, String message) {
            String errorHtml = """
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка подтверждения email</title>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');
        
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
        }
        
        body {
            background-color: #f8fafc;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 2rem;
            color: #1e293b;
            line-height: 1.5;
        }
        
        .error-card {
            max-width: 480px;
            width: 100%;
            background: white;
            border-radius: 16px;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 
                        0 10px 10px -5px rgba(0, 0, 0, 0.04);
            overflow: hidden;
            animation: fadeIn 0.5s ease-out;
        }
        
        .error-header {
            background: linear-gradient(135deg, #ef4444, #dc2626);
            color: white;
            padding: 2.5rem 2rem;
            text-align: center;
            position: relative;
        }
        
        .error-header h1 {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        
        .error-content {
            padding: 2.5rem 2rem;
            text-align: center;
        }
        
        .error-icon {
            font-size: 4rem;
            color: #ef4444;
            margin-bottom: 1.5rem;
            animation: bounce 0.6s;
        }
        
        .error-title {
            font-size: 1.25rem;
            font-weight: 600;
            margin-bottom: 1rem;
            color: #1e293b;
        }
        
        .error-message {
            color: #64748b;
            margin-bottom: 2rem;
        }
        
        .action-button {
            display: inline-block;
            background: #4f46e5;
            color: white;
            text-decoration: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            font-weight: 500;
            transition: all 0.3s ease;
            box-shadow: 0 4px 6px -1px rgba(79, 70, 229, 0.2), 
                        0 2px 4px -1px rgba(79, 70, 229, 0.12);
        }
        
        .action-button:hover {
            background: #4338ca;
            transform: translateY(-2px);
            box-shadow: 0 10px 15px -3px rgba(79, 70, 229, 0.3), 
                        0 4px 6px -2px rgba(79, 70, 229, 0.15);
        }
        
        .error-footer {
            margin-top: 2rem;
            font-size: 0.875rem;
            color: #94a3b8;
        }
        
        @keyframes fadeIn {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        @keyframes bounce {
            0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
            40% { transform: translateY(-20px); }
            60% { transform: translateY(-10px); }
        }
        
        .pulse {
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0.7); }
            70% { box-shadow: 0 0 0 12px rgba(239, 68, 68, 0); }
            100% { box-shadow: 0 0 0 0 rgba(239, 68, 68, 0); }
        }
    </style>
</head>
<body>
    <div class="error-card">
        <div class="error-header">
            <h1>Ошибка подтверждения</h1>
        </div>
        <div class="error-content">
            <div class="error-icon pulse">✗</div>
            <h2 class="error-title">Не удалось подтвердить email</h2>
            <p class="error-message">
                Ссылка для подтверждения недействительна или срок её действия истёк.<br>
                Пожалуйста, запросите новое письмо с подтверждением.
            </p>
            <a href="/register" class="action-button">Зарегистрироваться снова</a>
            <div class="error-footer">
                <p>Если проблема повторяется, свяжитесь с поддержкой</p>
            </div>
        </div>
    </div>
</body>
</html>
""";
            return errorHtml;
        }
    }

//    @GetMapping("/verify-email")
//    public String verifyEmail(@RequestParam String token) {
//        emailVerificationService.verifyEmail(token);
//        return "Email успешно подтвержден.";
//    }