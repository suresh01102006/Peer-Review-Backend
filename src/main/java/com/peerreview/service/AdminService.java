package com.peerreview.service;

import com.peerreview.dto.response.ProjectResponse;
import com.peerreview.dto.response.UserResponse;
import com.peerreview.model.User;
import com.peerreview.model.enums.ProjectStatus;
import com.peerreview.model.enums.Role;
import com.peerreview.repository.ProjectRepository;
import com.peerreview.repository.ReviewAssignmentRepository;
import com.peerreview.repository.ReviewRepository;
import com.peerreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewAssignmentRepository reviewAssignmentRepository;
    private final ProjectService projectService;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", userRepository.findByRoleNot(Role.ADMIN).size());
        stats.put("totalProjects", projectRepository.count());
        stats.put("approvedProjects", projectRepository.countByStatus(ProjectStatus.APPROVED));
        stats.put("pendingProjects", projectRepository.countByStatus(ProjectStatus.PENDING));
        stats.put("totalReviews", reviewRepository.count());
        stats.put("pendingReviews", reviewAssignmentRepository.count());
        return stats;
    }

    public List<Map<String, Object>> getStudentPerformance() {
        List<User> students = userRepository.findByRoleNot(Role.ADMIN);
        return students.stream().map(student -> {
            Map<String, Object> perf = new HashMap<>();
            perf.put("user", AuthService.toResponse(student));
            perf.put("projectsSubmitted", projectRepository.countBySubmittedBy(student));
            perf.put("reviewsGiven", reviewRepository.countByReviewer(student));
            Double avg = reviewRepository.findAvgRatingByReviewer(student);
            perf.put("avgRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
            long pending = reviewAssignmentRepository.countByReviewerAndCompleted(student, false);
            perf.put("pendingReviews", pending);
            return perf;
        }).collect(Collectors.toList());
    }

    public List<ProjectResponse> getAllSubmissions() {
        return projectService.findAll();
    }

    public String exportReportCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name,Email,Projects Submitted,Reviews Given,Avg Rating Given,Pending Reviews\n");
        List<Map<String, Object>> perf = getStudentPerformance();
        for (Map<String, Object> row : perf) {
            UserResponse u = (UserResponse) row.get("user");
            sb.append(String.format("%s,%s,%s,%s,%s,%s\n",
                    u.getName(), u.getEmail(),
                    row.get("projectsSubmitted"), row.get("reviewsGiven"),
                    row.get("avgRating"), row.get("pendingReviews")));
        }
        return sb.toString();
    }
}
