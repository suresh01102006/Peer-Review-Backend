package com.peerreview.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ReviewResponse {
    private Long id;
    private Long projectId;
    private String projectTitle;
    private UserResponse reviewer;
    private Integer qualityRating;
    private Integer creativityRating;
    private Integer presentationRating;
    private Double overallRating;
    private String comment;
    private Boolean anonymous;
    private String createdAt;
}
