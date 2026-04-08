package com.peerreview.dto.response;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private UserResponse sender;
    private String content;
    private String createdAt;
}
