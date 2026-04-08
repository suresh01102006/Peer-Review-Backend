package com.peerreview.dto.response;

import com.peerreview.model.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private Boolean read;
    private String createdAt;
}
