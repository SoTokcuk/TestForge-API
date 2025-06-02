package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateGenerationScopeDto {
    private Long projectId;
    private String requirements;
    private Boolean analyzeSourceCode;
    private Integer testCasesCount;
    private String aiStrategy;
}
