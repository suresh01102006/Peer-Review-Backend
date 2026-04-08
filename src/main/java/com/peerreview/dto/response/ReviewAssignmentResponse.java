package com.peerreview.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ReviewAssignmentResponse {
    private Long id;
    private ProjectResponse project;
    private UserResponse reviewer;
    private Boolean completed;
    private String assignedAt;
}
