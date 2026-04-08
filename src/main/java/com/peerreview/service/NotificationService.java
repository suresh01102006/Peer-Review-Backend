package com.peerreview.service;

import com.peerreview.dto.response.NotificationResponse;
import com.peerreview.exception.ResourceNotFoundException;
import com.peerreview.model.Notification;
import com.peerreview.model.User;
import com.peerreview.model.enums.NotificationType;
import com.peerreview.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User user, NotificationType type, String message) {
        if (user == null) return;
        Notification n = Notification.builder().user(user).type(type).message(message).read(false).build();
        notificationRepository.save(n);
    }

    public List<NotificationResponse> getMyNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public long countUnread(User user) {
        return notificationRepository.countByUserAndRead(user, false);
    }

    public void markAsRead(Long id, User user) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndRead(user, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId()).type(n.getType()).message(n.getMessage()).read(n.getRead())
                .createdAt(n.getCreatedAt() != null ? n.getCreatedAt().toString() : null)
                .build();
    }
}
