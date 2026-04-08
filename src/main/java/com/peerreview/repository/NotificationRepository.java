package com.peerreview.repository;

import com.peerreview.model.Notification;
import com.peerreview.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndRead(User user, Boolean read);
    long countByUserAndRead(User user, Boolean read);
}
