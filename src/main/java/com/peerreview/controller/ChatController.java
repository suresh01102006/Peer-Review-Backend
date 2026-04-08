package com.peerreview.controller;

import com.peerreview.dto.request.ConversationRequest;
import com.peerreview.dto.request.MessageRequest;
import com.peerreview.dto.response.ConversationResponse;
import com.peerreview.dto.response.MessageResponse;
import com.peerreview.model.User;
import com.peerreview.service.ChatService;
import com.peerreview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponse>> getConversations(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(chatService.getMyConversations(user));
    }

    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponse> createConversation(@RequestBody ConversationRequest req,
                                                                    @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(chatService.createConversation(req, user));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getMessages(id));
    }

    @PostMapping("/conversations/{id}/messages")
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable Long id,
                                                        @RequestBody MessageRequest req,
                                                        @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        MessageResponse msg = chatService.sendMessage(id, req, user);
        // Broadcast via WebSocket
        messagingTemplate.convertAndSend("/topic/chat/" + id, msg);
        return ResponseEntity.ok(msg);
    }

    @MessageMapping("/chat/{conversationId}")
    public void handleWsMessage(@DestinationVariable Long conversationId,
                                 @Payload MessageRequest req,
                                 Principal principal) {
        if (principal == null) return;
        User user = userService.getByEmail(principal.getName());
        MessageResponse msg = chatService.sendMessage(conversationId, req, user);
        messagingTemplate.convertAndSend("/topic/chat/" + conversationId, msg);
    }
}
