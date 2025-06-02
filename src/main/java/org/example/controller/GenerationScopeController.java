package org.example.controller;

import org.example.dto.CreateGenerationScopeDto;
import org.example.entity.GenerationScope;
import org.example.service.GenerationScopeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/generation-scopes")
public class GenerationScopeController {

    private final GenerationScopeService generationScopeService;

    public GenerationScopeController(GenerationScopeService generationScopeService) {
        this.generationScopeService = generationScopeService;
    }

    @PostMapping
    public ResponseEntity<GenerationScope> createGenerationScope(
            @RequestBody CreateGenerationScopeDto dto) {

        GenerationScope scope = new GenerationScope();
        scope.setRequirements(dto.getRequirements());
        scope.setAnalyzeSourceCode(dto.getAnalyzeSourceCode());
        scope.setTestCasesCount(dto.getTestCasesCount());
        scope.setAiStrategy(dto.getAiStrategy());

        GenerationScope createdScope = generationScopeService.createGenerationScope(
                dto.getProjectId(), scope);

        return ResponseEntity.ok(createdScope);
    }
}