package org.example.service;

import org.example.dto.testCase.UpdateTestCaseDto;
import org.example.entity.TestCase;
import org.example.repository.TestCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;

    public TestCaseService(TestCaseRepository testCaseRepository) {
        this.testCaseRepository = testCaseRepository;
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
}
