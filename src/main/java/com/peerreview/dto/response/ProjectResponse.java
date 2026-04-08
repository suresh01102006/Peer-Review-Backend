package com.peerreview.dto.response;

import com.peerreview.model.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProjectResponse {
    private Long id;
    private String title;
    private String description;
    private String tags;
    private String fileUrl;
    private String githubLink;
    private UserResponse submittedBy;
    private AssignmentResponse assignment;
    private ProjectStatus status;
    private String createdAt;
    private Double avgRating;
    private Long reviewCount;
}
