package com.peerreview.service;

import com.peerreview.dto.request.ReviewAssignmentRequest;
import com.peerreview.dto.response.ReviewAssignmentResponse;
import com.peerreview.model.Project;
import com.peerreview.model.ReviewAssignment;
import com.peerreview.model.User;
import com.peerreview.model.enums.NotificationType;
import com.peerreview.model.enums.Role;
import com.peerreview.repository.ReviewAssignmentRepository;
import com.peerreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewAssignmentService {

    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final ProjectService projectService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ReviewAssignmentResponse assign(ReviewAssignmentRequest req) {
        Project project = projectService.findById(req.getProjectId());
        User reviewer = userService.findById(req.getReviewerId());
        if (reviewAssignmentRepository.existsByProjectAndReviewer(project, reviewer)) {
            throw new IllegalArgumentException("Reviewer already assigned to this project");
        }
        ReviewAssignment ra = ReviewAssignment.builder()
                .project(project).reviewer(reviewer).completed(false).build();
        ReviewAssignment saved = reviewAssignmentRepository.save(ra);
        notificationService.createNotification(reviewer, NotificationType.REVIEW_ASSIGNED,
                "You have been assigned to review: " + project.getTitle());
        return toResponse(saved);
    }

    public List<ReviewAssignmentResponse> autoAssign(Long projectId) {
        Project project = projectService.findById(projectId);
        List<User> students = userRepository.findByRoleNot(Role.ADMIN);
        // Exclude the project owner
        students = students.stream()
                .filter(s -> !s.getId().equals(project.getSubmittedBy().getId()))
                .filter(s -> !reviewAssignmentRepository.existsByProjectAndReviewer(project, s))
                .collect(Collectors.toList());

        List<ReviewAssignmentResponse> assigned = new ArrayList<>();
        int count = 0;
        for (User student : students) {
            if (count >= 3) break;
            ReviewAssignment ra = ReviewAssignment.builder()
                    .project(project).reviewer(student).completed(false).build();
            assigned.add(toResponse(reviewAssignmentRepository.save(ra)));
            notificationService.createNotification(student, NotificationType.REVIEW_ASSIGNED,
                    "You have been assigned to review: " + project.getTitle());
            count++;
        }
        return assigned;
    }

    public List<ReviewAssignmentResponse> getMyAssignments(User reviewer) {
        return reviewAssignmentRepository.findByReviewer(reviewer).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<ReviewAssignmentResponse> getPendingAssignments(User reviewer) {
        return reviewAssignmentRepository.findByReviewerAndCompleted(reviewer, false).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<ReviewAssignmentResponse> getByProject(Long projectId) {
        Project project = projectService.findById(projectId);
        return reviewAssignmentRepository.findByProject(project).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private ReviewAssignmentResponse toResponse(ReviewAssignment ra) {
        return ReviewAssignmentResponse.builder()
                .id(ra.getId())
                .project(projectService.toResponse(ra.getProject()))
                .reviewer(AuthService.toResponse(ra.getReviewer()))
                .completed(ra.getCompleted())
                .assignedAt(ra.getAssignedAt() != null ? ra.getAssignedAt().toString() : null)
                .build();
    }
}
