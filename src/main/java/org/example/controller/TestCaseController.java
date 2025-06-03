package org.example.controller;

import org.example.dto.testCase.TestCaseDto;
import org.example.dto.testCase.UpdateTestCaseDto;
import org.example.entity.TestCase;
import org.example.service.TestCaseService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test-cases")
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final ModelMapper modelMapper;

    public TestCaseController(TestCaseService testCaseService, ModelMapper modelMapper) {
        this.testCaseService = testCaseService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseDto> getTestCaseById(@PathVariable Long id) {
        TestCase testCase = testCaseService.getTestCaseById(id);
        TestCaseDto dto = modelMapper.map(testCase, TestCaseDto.class);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/by-scope/{generationScopeId}")
    public ResponseEntity<List<TestCase>> getAllByGenerationScopeId(
            @PathVariable Long generationScopeId) {
        return ResponseEntity.ok(testCaseService.getAllByGenerationScopeId(generationScopeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestCase> updateTestCase(
            @PathVariable Long id,
            @RequestBody UpdateTestCaseDto dto) {
        return ResponseEntity.ok(testCaseService.updateTestCase(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }
}
