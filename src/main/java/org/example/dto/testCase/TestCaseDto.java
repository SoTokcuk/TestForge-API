package org.example.dto.testCase;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TestCaseDto {
    private Long id;
    private String status;
    private String response;
    private LocalDateTime createdAt;
    //private Long generationScopeId; // Только ID, а не всю сущность
}
