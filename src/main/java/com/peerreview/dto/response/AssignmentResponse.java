package com.peerreview.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class AssignmentResponse {
    private Long id;
    private String title;
    private String description;
    private String deadline;
    private String category;
    private UserResponse createdBy;
    private String createdAt;
}
