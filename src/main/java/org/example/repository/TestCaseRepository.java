package org.example.repository;

import org.example.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    @Transactional
    default void deleteAllByGenerationScopeId(Long generationScopeId) {
        List<TestCase> testCases = findByGenerationScopeId(generationScopeId);
        deleteAll(testCases);
    }

    List<TestCase> findByGenerationScopeId(Long generationScopeId);
}
