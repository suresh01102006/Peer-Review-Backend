package com.peerreview.controller;

import com.peerreview.dto.request.ReviewRequest;
import com.peerreview.dto.response.ReviewResponse;
import com.peerreview.model.User;
import com.peerreview.service.ReviewService;
import com.peerreview.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReviewResponse>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(reviewService.getByProject(projectId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> myReviews(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(reviewService.getMyReviews(user));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewRequest req,
                                                  @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(reviewService.create(req, user));
    }
}
