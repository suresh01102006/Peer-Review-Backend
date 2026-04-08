package com.peerreview.repository;

import com.peerreview.model.Project;
import com.peerreview.model.Review;
import com.peerreview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProject(Project project);
    List<Review> findByReviewer(User reviewer);
    Optional<Review> findByProjectAndReviewer(Project project, User reviewer);
    boolean existsByProjectAndReviewer(Project project, User reviewer);

    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.project = :project")
    Double findAvgRatingByProject(@Param("project") Project project);

    @Query("SELECT AVG(r.overallRating) FROM Review r WHERE r.reviewer = :user")
    Double findAvgRatingByReviewer(@Param("user") User user);

    long countByReviewer(User reviewer);
}
