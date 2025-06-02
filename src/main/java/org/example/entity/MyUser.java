package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "my_user")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    private String password;
    private String role;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private boolean isEmailVerified = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public MyUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}