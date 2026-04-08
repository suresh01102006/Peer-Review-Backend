package com.peerreview.dto.response;

import com.peerreview.model.enums.ConversationType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @Builder
public class ConversationResponse {
    private Long id;
    private String name;
    private ConversationType type;
    private List<UserResponse> participants;
    private String createdAt;
    private MessageResponse lastMessage;
}
