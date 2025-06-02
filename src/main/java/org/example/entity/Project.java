package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "programming_language", length = 50)
    private String programmingLanguage;

    @Column(name = "code_transfer_type", length = 50)
    private String codeTransferType;

    @Column(name = "git_url", length = 255)
    private String gitUrl;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Конструкторы
    public Project() {
        this.createdAt = LocalDateTime.now();
    }

    public Project(Long userId, String name) {
        this();
        this.userId = userId;
        this.name = name;
    }


    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", programmingLanguage='" + programmingLanguage + '\'' +
                ", codeTransferType='" + codeTransferType + '\'' +
                ", gitUrl='" + gitUrl + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}