package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "generation_scopes")
public class GenerationScope {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "CREATED"; // CREATED, PROCESSING, COMPLETED, FAILED

    @Column(name = "git_url", length = 255)
    private String gitUrl;

    @Column(name = "test_cases_count")
    private Integer testCasesCount;

    @Column(name = "analyze_source_code", nullable = false)
    private Boolean analyzeSourceCode = false;

    @Column(name = "ai_strategy", length = 50)
    private String aiStrategy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Конструкторы
    public GenerationScope() {}

    public GenerationScope(Project project, String requirements) {
        this.project = project;
        this.requirements = requirements;
        this.gitUrl = project.getGitUrl(); // Берем URL из проекта при создании
    }


    // Логика перед сохранением
    @PrePersist
    protected void onPrePersist() {
        if (this.gitUrl == null && this.project != null) {
            this.gitUrl = this.project.getGitUrl();
        }
    }

    @Override
    public String toString() {
        return "GenerationScope{" +
                "id=" + id +
                ", projectId=" + (project != null ? project.getId() : null) +
                ", status='" + status + '\'' +
                ", testCasesCount=" + testCasesCount +
                '}';
    }
}