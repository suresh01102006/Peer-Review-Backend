package com.peerreview.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank
    private String title;
    private String description;
    private String tags;
    private String githubLink;
    private Long assignmentId;
}
