package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.example.dto.testCase.TestCaseDto;
import org.example.dto.testCase.UpdateTestCaseDto;
import org.example.entity.TestCase;
import org.example.repository.TestCaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public TestCaseService(TestCaseRepository testCaseRepository, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.testCaseRepository = testCaseRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public TestCase getTestCaseById(Long id) {
        return testCaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TestCase not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TestCase> getAllByGenerationScopeId(Long generationScopeId) {
        return testCaseRepository.findByGenerationScopeId(generationScopeId);
    }

    @Transactional
    public TestCase updateTestCase(Long id, UpdateTestCaseDto dto) {
        TestCase testCase = getTestCaseById(id);

        if (dto.getStatus() != null) {
            testCase.setStatus(dto.getStatus());
        }

        if (dto.getResponse() != null) {
            testCase.setResponse(dto.getResponse());
        }

        return testCaseRepository.save(testCase);
    }

    @Transactional
    public void deleteTestCase(Long id) {
        TestCase testCase = getTestCaseById(id);
        testCaseRepository.delete(testCase);
    }

    public byte[] exportToJson(List<TestCase> testCases) {
        try {
            List<TestCaseDto> dtos = testCases.stream()
                    .map(testCase -> modelMapper.map(testCase, TestCaseDto.class))
                    .collect(Collectors.toList());
            return objectMapper.writeValueAsBytes(dtos);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate JSON", e);
        }
    }

    public byte[] exportToCsv(List<TestCase> testCases) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(out, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.withHeader(
                             "ID", "Status", "Response", "Created At"))) {

            for (TestCase testCase : testCases) {
                csvPrinter.printRecord(
                        testCase.getId(),
                        testCase.getStatus(),
                        testCase.getResponse(),
                        testCase.getCreatedAt());
            }

            csvPrinter.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }
}
