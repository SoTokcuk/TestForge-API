package org.example.dto.generationScope;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GenerationScopeDto {
    private Long id;
    private String requirements;
    private String status;
    private Integer testCasesCount;
    private LocalDateTime createdAt;

}
