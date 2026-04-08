package com.peerreview.dto.request;

import com.peerreview.model.enums.ConversationType;
import lombok.Data;

import java.util.List;

@Data
public class ConversationRequest {
    private ConversationType type = ConversationType.DM;
    private List<Long> participantIds;
    private String name;
}
