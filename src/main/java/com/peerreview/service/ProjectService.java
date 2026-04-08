package com.peerreview.service;

import com.peerreview.dto.request.ProjectRequest;
import com.peerreview.dto.response.AssignmentResponse;
import com.peerreview.dto.response.ProjectResponse;
import com.peerreview.exception.ResourceNotFoundException;
import com.peerreview.model.Assignment;
import com.peerreview.model.Project;
import com.peerreview.model.User;
import com.peerreview.model.enums.ProjectStatus;
import com.peerreview.repository.ProjectRepository;
import com.peerreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;
    private final AssignmentService assignmentService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public ProjectResponse submit(ProjectRequest req, MultipartFile file, User user) {
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            fileUrl = saveFile(file);
        }
        Project project = Project.builder()
                .title(req.getTitle()).description(req.getDescription())
                .tags(req.getTags()).githubLink(req.getGithubLink())
                .fileUrl(fileUrl).submittedBy(user).status(ProjectStatus.PENDING)
                .assignment(req.getAssignmentId() != null ? assignmentService.getById(req.getAssignmentId()) : null)
                .build();
        return toResponse(projectRepository.save(project));
    }

    public List<ProjectResponse> search(String search, String tag, String status) {
        ProjectStatus ps = null;
        try { if (status != null && !status.isEmpty()) ps = ProjectStatus.valueOf(status.toUpperCase()); }
        catch (Exception ignored) {}
        return projectRepository.searchProjects(
                (search == null || search.isEmpty()) ? null : search,
                (tag == null || tag.isEmpty()) ? null : tag, ps)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ProjectResponse getById(Long id) { return toResponse(findById(id)); }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    public List<ProjectResponse> getMyProjects(User user) {
        return projectRepository.findBySubmittedBy(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public ProjectResponse updateStatus(Long id, ProjectStatus status) {
        Project p = findById(id);
        p.setStatus(status);
        return toResponse(projectRepository.save(p));
    }

    public void delete(Long id) { projectRepository.deleteById(id); }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private String saveFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String originalFilename = Objects.requireNonNullElse(file.getOriginalFilename(), "file");
            String safeFilename = Paths.get(originalFilename).getFileName().toString()
                    .replaceAll("[\\r\\n]", "_");
            String filename = UUID.randomUUID() + "_" + safeFilename;
            Path targetPath = uploadPath.resolve(filename).normalize();
            if (!targetPath.startsWith(uploadPath)) {
                throw new RuntimeException("Invalid file path");
            }
            file.transferTo(targetPath);
            return "/api/files/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    public ProjectResponse toResponse(Project p) {
        Double avg = reviewRepository.findAvgRatingByProject(p);
        long reviewCount = reviewRepository.findByProject(p).size();
        return ProjectResponse.builder()
                .id(p.getId()).title(p.getTitle()).description(p.getDescription())
                .tags(p.getTags()).fileUrl(p.getFileUrl()).githubLink(p.getGithubLink())
                .submittedBy(p.getSubmittedBy() != null ? AuthService.toResponse(p.getSubmittedBy()) : null)
                .assignment(p.getAssignment() != null ? assignmentToResponse(p.getAssignment()) : null)
                .status(p.getStatus())
                .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : null)
                .avgRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null)
                .reviewCount(reviewCount)
                .build();
    }

    private AssignmentResponse assignmentToResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId()).title(a.getTitle()).description(a.getDescription())
                .deadline(a.getDeadline() != null ? a.getDeadline().toString() : null)
                .category(a.getCategory())
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .build();
    }
}
