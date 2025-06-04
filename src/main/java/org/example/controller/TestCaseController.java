package org.example.controller;

import org.example.dto.testCase.TestCaseDto;
import org.example.dto.testCase.UpdateTestCaseDto;
import org.example.entity.TestCase;
import org.example.service.TestCaseService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/by-scope/{scopeId}")
    public ResponseEntity<List<TestCaseDto>> getAllTestCasesByScope(@PathVariable Long scopeId) {
        List<TestCase> testCases = testCaseService.getAllByGenerationScopeId(scopeId);
        List<TestCaseDto> dtos = testCases.stream()
                .map(tc -> modelMapper.map(tc, TestCaseDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestCaseDto> updateTestCase(
            @PathVariable Long id,
            @RequestBody UpdateTestCaseDto dto) {
        TestCase updatedTestCase = testCaseService.updateTestCase(id, dto);
        TestCaseDto responseDto = modelMapper.map(updatedTestCase, TestCaseDto.class);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTestCase(@PathVariable Long id) {
        testCaseService.deleteTestCase(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/by-scope/{generationScopeId}/export", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE
    })
    public ResponseEntity<byte[]> exportTestCasesByScope(
            @PathVariable Long generationScopeId,
            @RequestParam(defaultValue = "json") String format) {

        List<TestCase> testCases = testCaseService.getAllByGenerationScopeId(generationScopeId);

        byte[] fileContent;
        String filename;
        MediaType contentType;

        if ("csv".equalsIgnoreCase(format)) {
            fileContent = testCaseService.exportToCsv(testCases);
            filename = "test-cases-scope-" + generationScopeId + ".csv";
            contentType = MediaType.parseMediaType("text/csv");
        } else {
            fileContent = testCaseService.exportToJson(testCases);
            filename = "test-cases-scope-" + generationScopeId + ".json";
            contentType = MediaType.APPLICATION_JSON;
        }

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .body(fileContent);
    }
}
