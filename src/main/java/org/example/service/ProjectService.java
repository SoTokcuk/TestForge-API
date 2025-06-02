package org.example.service;

import org.example.entity.Project;
import org.example.repository.ProjectRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<Project> getAllProjects(String name, LocalDateTime startDate, LocalDateTime endDate,
                                        String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return projectRepository.findWithFiltersNative(name, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    @Transactional
    public Project createProject(Project project) {
        project.setStatus("active");
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long id, Project projectDetails) {
        Project project = getProjectById(id);

        project.setName(projectDetails.getName());
        project.setDescription(projectDetails.getDescription());
        project.setGitUrl(projectDetails.getGitUrl());
        project.setStatus(projectDetails.getStatus());
        project.setProgrammingLanguage(projectDetails.getProgrammingLanguage());
        project.setCodeTransferType(projectDetails.getCodeTransferType());

        return projectRepository.save(project);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = getProjectById(id);
        projectRepository.delete(project);
    }
}
