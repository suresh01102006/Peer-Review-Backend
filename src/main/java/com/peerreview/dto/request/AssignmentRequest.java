package com.peerreview.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentRequest {
    @NotBlank
    private String title;
    private String description;
    private LocalDateTime deadline;
    private String category;
}
