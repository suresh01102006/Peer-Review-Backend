package com.peerreview.dto.request;

import lombok.Data;

@Data
public class ReviewAssignmentRequest {
    private Long projectId;
    private Long reviewerId;
}
