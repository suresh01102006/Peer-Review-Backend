package com.peerreview.service;

import com.peerreview.dto.request.ReviewRequest;
import com.peerreview.dto.response.ReviewResponse;
import com.peerreview.dto.response.UserResponse;
import com.peerreview.exception.ResourceNotFoundException;
import com.peerreview.model.Project;
import com.peerreview.model.Review;
import com.peerreview.model.User;
import com.peerreview.model.enums.NotificationType;
import com.peerreview.repository.ReviewAssignmentRepository;
import com.peerreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProjectService projectService;
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final NotificationService notificationService;

    public ReviewResponse create(ReviewRequest req, User reviewer) {
        Project project = projectService.findById(req.getProjectId());
        if (reviewRepository.existsByProjectAndReviewer(project, reviewer)) {
            throw new IllegalArgumentException("You have already reviewed this project");
        }
        Review review = Review.builder()
                .project(project).reviewer(reviewer)
                .qualityRating(req.getQualityRating())
                .creativityRating(req.getCreativityRating())
                .presentationRating(req.getPresentationRating())
                .comment(req.getComment())
                .anonymous(req.getAnonymous() != null ? req.getAnonymous() : false)
                .build();
        Review saved = reviewRepository.save(review);

        // Mark assignment complete
        reviewAssignmentRepository.findByProjectAndReviewer(project, reviewer)
                .ifPresent(ra -> { ra.setCompleted(true); reviewAssignmentRepository.save(ra); });

        // Notify project owner
        if (project.getSubmittedBy() != null) {
            notificationService.createNotification(project.getSubmittedBy(),
                    NotificationType.REVIEW_RECEIVED,
                    "Your project '" + project.getTitle() + "' received a new review!");
        }
        return toResponse(saved);
    }

    public List<ReviewResponse> getByProject(Long projectId) {
        Project project = projectService.findById(projectId);
        return reviewRepository.findByProject(project).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<ReviewResponse> getMyReviews(User reviewer) {
        return reviewRepository.findByReviewer(reviewer).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public ReviewResponse toResponse(Review r) {
        UserResponse reviewer = null;
        if (!r.getAnonymous()) {
            reviewer = AuthService.toResponse(r.getReviewer());
        }
        return ReviewResponse.builder()
                .id(r.getId())
                .projectId(r.getProject().getId())
                .projectTitle(r.getProject().getTitle())
                .reviewer(reviewer)
                .qualityRating(r.getQualityRating())
                .creativityRating(r.getCreativityRating())
                .presentationRating(r.getPresentationRating())
                .overallRating(r.getOverallRating())
                .comment(r.getComment())
                .anonymous(r.getAnonymous())
                .createdAt(r.getCreatedAt() != null ? r.getCreatedAt().toString() : null)
                .build();
    }
}
