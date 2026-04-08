package com.peerreview.service;

import com.peerreview.dto.request.ConversationRequest;
import com.peerreview.dto.request.MessageRequest;
import com.peerreview.dto.response.ConversationResponse;
import com.peerreview.dto.response.MessageResponse;
import com.peerreview.exception.ResourceNotFoundException;
import com.peerreview.model.Conversation;
import com.peerreview.model.Message;
import com.peerreview.model.User;
import com.peerreview.model.enums.ConversationType;
import com.peerreview.repository.ConversationRepository;
import com.peerreview.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public ConversationResponse createConversation(ConversationRequest req, User creator) {
        List<User> participants = new ArrayList<>();
        participants.add(creator);
        if (req.getParticipantIds() != null) {
            req.getParticipantIds().forEach(id -> {
                if (!id.equals(creator.getId())) participants.add(userService.findById(id));
            });
        }
        // Check DM already exists
        if (req.getType() == ConversationType.DM && participants.size() == 2) {
            List<Conversation> existing = conversationRepository.findDmBetween(participants.get(0), participants.get(1));
            if (!existing.isEmpty()) return toResponse(existing.get(0));
        }
        Conversation convo = Conversation.builder()
                .type(req.getType()).name(req.getName()).participants(participants).build();
        return toResponse(conversationRepository.save(convo));
    }

    public List<ConversationResponse> getMyConversations(User user) {
        return conversationRepository.findByParticipant(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public MessageResponse sendMessage(Long conversationId, MessageRequest req, User sender) {
        Conversation convo = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        Message msg = Message.builder().conversation(convo).sender(sender).content(req.getContent()).build();
        return toMessageResponse(messageRepository.save(msg));
    }

    public List<MessageResponse> getMessages(Long conversationId) {
        Conversation convo = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        return messageRepository.findByConversationOrderByCreatedAtAsc(convo).stream()
                .map(this::toMessageResponse).collect(Collectors.toList());
    }

    private ConversationResponse toResponse(Conversation c) {
        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(c);
        MessageResponse last = messages.isEmpty() ? null : toMessageResponse(messages.get(messages.size() - 1));
        return ConversationResponse.builder()
                .id(c.getId()).name(c.getName()).type(c.getType())
                .participants(c.getParticipants().stream().map(AuthService::toResponse).collect(Collectors.toList()))
                .createdAt(c.getCreatedAt() != null ? c.getCreatedAt().toString() : null)
                .lastMessage(last).build();
    }

    public MessageResponse toMessageResponse(Message m) {
        return MessageResponse.builder()
                .id(m.getId()).conversationId(m.getConversation().getId())
                .sender(AuthService.toResponse(m.getSender()))
                .content(m.getContent())
                .createdAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null)
                .build();
    }
}
