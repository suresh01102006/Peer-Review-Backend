package com.peerreview.repository;

import com.peerreview.model.Project;
import com.peerreview.model.ReviewAssignment;
import com.peerreview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewAssignmentRepository extends JpaRepository<ReviewAssignment, Long> {
    List<ReviewAssignment> findByReviewer(User reviewer);
    List<ReviewAssignment> findByReviewerAndCompleted(User reviewer, Boolean completed);
    List<ReviewAssignment> findByProject(Project project);
    Optional<ReviewAssignment> findByProjectAndReviewer(Project project, User reviewer);
    boolean existsByProjectAndReviewer(Project project, User reviewer);
    long countByReviewerAndCompleted(User reviewer, Boolean completed);
}
