package com.peerreview.controller;

import com.peerreview.dto.response.NotificationResponse;
import com.peerreview.model.User;
import com.peerreview.service.NotificationService;
import com.peerreview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(notificationService.getMyNotifications(user));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(user)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        notificationService.markAsRead(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllRead(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        notificationService.markAllAsRead(user);
        return ResponseEntity.noContent().build();
    }
}
