package org.example.controller;

import org.example.dto.generationScope.CreateGenerationScopeDto;
import org.example.dto.generationScope.GenerationScopeDto;
import org.example.dto.generationScope.UpdateGenerationScopeDto;
import org.example.entity.GenerationScope;
import org.example.service.GenerationScopeService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/generation-scopes")
public class GenerationScopeController {

    private final GenerationScopeService generationScopeService;
    private final ModelMapper modelMapper;

    public GenerationScopeController(
            GenerationScopeService generationScopeService,
            ModelMapper modelMapper) {
        this.generationScopeService = generationScopeService;
        this.modelMapper = modelMapper;
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

    @GetMapping("/by-project/{projectId}")
    public ResponseEntity<List<GenerationScopeDto>> getAllForProject(
            @PathVariable Long projectId) {
        List<GenerationScope> scopes = generationScopeService.getAllForProject(projectId);
        List<GenerationScopeDto> dtos = scopes.stream()
                .map(scope -> modelMapper.map(scope, GenerationScopeDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenerationScopeDto> updateGenerationScope(
            @PathVariable Long id,
            @RequestBody UpdateGenerationScopeDto dto) {
        GenerationScope updatedScope = generationScopeService.updateGenerationScope(id, dto);
        GenerationScopeDto responseDto = modelMapper.map(updatedScope, GenerationScopeDto.class);
        return ResponseEntity.ok(responseDto);
    }
}