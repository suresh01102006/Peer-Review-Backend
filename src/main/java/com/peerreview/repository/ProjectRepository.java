package com.peerreview.repository;

import com.peerreview.model.Project;
import com.peerreview.model.User;
import com.peerreview.model.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findBySubmittedBy(User user);
    List<Project> findByStatus(ProjectStatus status);

    @Query("SELECT p FROM Project p WHERE " +
           "(:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:tag IS NULL OR LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))) " +
           "AND (:status IS NULL OR p.status = :status)")
    List<Project> searchProjects(@Param("search") String search,
                                  @Param("tag") String tag,
                                  @Param("status") ProjectStatus status);

    long countBySubmittedBy(User user);
    long countByStatus(ProjectStatus status);
}
