package org.example.service;

import org.example.dto.generationScope.UpdateGenerationScopeDto;
import org.example.entity.GenerationScope;
import org.example.entity.Project;
import org.example.entity.TestCase;
import org.example.entity.component.AIClient;
import org.example.repository.GenerationScopeRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.TestCaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenerationScopeService {

    private final GenerationScopeRepository generationScopeRepository;
    private final TestCaseRepository testCaseRepository;
    private final ProjectRepository projectRepository;
    private final AIClient aiClient;
    private final ModelMapper modelMapper;

    public GenerationScopeService(GenerationScopeRepository generationScopeRepository, ProjectRepository projectRepository,
                                  TestCaseRepository testCaseRepository,
                                  AIClient aiClient,
                                  ModelMapper modelMapper) {
        this.generationScopeRepository = generationScopeRepository;
        this.projectRepository = projectRepository;
        this.testCaseRepository = testCaseRepository;
        this.aiClient = aiClient;
        this.modelMapper = modelMapper;
    }

    @Transactional(readOnly = true)
    public List<GenerationScope> getAllForProject(Long projectId) {
        return generationScopeRepository.findByProjectId(projectId);
    }

    @Transactional
    public GenerationScope createGenerationScope(Long projectId, GenerationScope scope) {
        // Получаем проект из БД
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        // Устанавливаем связь с проектом
        scope.setProject(project);

        // Если gitUrl не указан, берем из проекта
        if (scope.getGitUrl() == null) {
            scope.setGitUrl(project.getGitUrl());
        }

        // Сохраняем scope в БД
        GenerationScope savedScope = generationScopeRepository.save(scope);

        // Запускаем асинхронную обработку
        processGenerationAsync(savedScope.getId());

        return savedScope;
    }

    @Async
    public void processGenerationAsync(Long scopeId) {
        GenerationScope scope = generationScopeRepository.findById(scopeId)
                .orElseThrow(() -> new RuntimeException("GenerationScope not found"));

        try {
            // 1. Подготовка данных для AI
            String prompt = buildPrompt(scope);

            // 2. Вызов AI API
            //LocalDateTime startTime = LocalDateTime.now();
            String aiResponse = aiClient.generateTestCases(prompt, scope.getTestCasesCount(), scope.getAiStrategy());
            //Duration processingTime = Duration.between(startTime, LocalDateTime.now());

            // 3. Сохранение результата
            TestCase testCase = new TestCase();
            testCase.setGenerationScope(scope);
            testCase.setResponse(aiResponse);
            testCaseRepository.save(testCase);

            // 4. Обновление статуса
            scope.setStatus("COMPLETED");
            generationScopeRepository.save(scope);

        } catch (Exception e) {
            // Обработка ошибок
            scope.setStatus("FAILED");
            generationScopeRepository.save(scope);
            throw new RuntimeException("AI processing failed", e);
        }
    }

    private String buildPrompt(GenerationScope scope) {
        return String.format(
                "Generate %d test cases for: %s%s",
                scope.getTestCasesCount(),
                scope.getRequirements(),
                scope.getAnalyzeSourceCode() ?
                        "\n\nAnalyze source code from: " + scope.getGitUrl() : ""
//                ,
//                scope.getAiStrategy() != null ?
//                        "\n\nAI Strategy: " + scope.getAiStrategy() : ""
        );
    }

    public GenerationScope updateGenerationScope(Long id, UpdateGenerationScopeDto dto) {
        GenerationScope scope = generationScopeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(dto, scope);

        return generationScopeRepository.save(scope);
    }

    @Transactional
    public void deleteGenerationScope(Long id) {
        GenerationScope scope = generationScopeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("GenerationScope not found with id: " + id));

        // Если есть связанные TestCases, сначала удаляем их
        testCaseRepository.deleteAllByGenerationScopeId(id);

        generationScopeRepository.delete(scope);
    }
}
