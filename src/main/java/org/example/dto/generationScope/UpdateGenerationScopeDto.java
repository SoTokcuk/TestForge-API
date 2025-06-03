package org.example.dto.generationScope;

import lombok.Data;

@Data
public class UpdateGenerationScopeDto {
    private String requirements;
    private String status;
    private Integer testCasesCount;
}
