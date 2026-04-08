package com.peerreview.controller;

import com.peerreview.dto.request.ReviewAssignmentRequest;
import com.peerreview.dto.response.ReviewAssignmentResponse;
import com.peerreview.model.User;
import com.peerreview.service.ReviewAssignmentService;
import com.peerreview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review-assignments")
@RequiredArgsConstructor
public class ReviewAssignmentController {

    private final ReviewAssignmentService reviewAssignmentService;
    private final UserService userService;

    @GetMapping("/my")
    public ResponseEntity<List<ReviewAssignmentResponse>> myAssignments(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(reviewAssignmentService.getMyAssignments(user));
    }

    @GetMapping("/my/pending")
    public ResponseEntity<List<ReviewAssignmentResponse>> myPending(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(reviewAssignmentService.getPendingAssignments(user));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReviewAssignmentResponse>> getByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(reviewAssignmentService.getByProject(projectId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewAssignmentResponse> assign(@RequestBody ReviewAssignmentRequest req) {
        return ResponseEntity.ok(reviewAssignmentService.assign(req));
    }

    @PostMapping("/auto/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewAssignmentResponse>> autoAssign(@PathVariable Long projectId) {
        return ResponseEntity.ok(reviewAssignmentService.autoAssign(projectId));
    }
}
