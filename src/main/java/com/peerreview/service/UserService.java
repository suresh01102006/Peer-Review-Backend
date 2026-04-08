package com.peerreview.service;

import com.peerreview.dto.request.UpdateProfileRequest;
import com.peerreview.dto.response.UserResponse;
import com.peerreview.exception.ResourceNotFoundException;
import com.peerreview.model.User;
import com.peerreview.repository.ReviewRepository;
import com.peerreview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse getById(Long id) {
        return AuthService.toResponse(findById(id));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    public UserResponse updateProfile(Long id, UpdateProfileRequest req, String currentEmail) {
        User user = findById(id);
        if (!user.getEmail().equals(currentEmail)) {
            throw new IllegalArgumentException("Cannot update another user's profile");
        }
        if (req.getName() != null) user.setName(req.getName());
        if (req.getBio() != null) user.setBio(req.getBio());
        if (req.getSkills() != null) user.setSkills(req.getSkills());
        if (req.getAvatarUrl() != null) user.setAvatarUrl(req.getAvatarUrl());
        return AuthService.toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AuthService::toResponse).collect(Collectors.toList());
    }

    public List<UserResponse> getAllStudents() {
        return userRepository.findByRoleNot(com.peerreview.model.enums.Role.ADMIN).stream()
                .map(AuthService::toResponse).collect(Collectors.toList());
    }

    public Map<String, Object> getUserStats(Long id) {
        User user = findById(id);
        Map<String, Object> stats = new HashMap<>();
        Double avgRating = reviewRepository.findAvgRatingByReviewer(user);
        long reviewsGiven = reviewRepository.countByReviewer(user);
        stats.put("avgRatingGiven", avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        stats.put("reviewsGiven", reviewsGiven);
        return stats;
    }
}
