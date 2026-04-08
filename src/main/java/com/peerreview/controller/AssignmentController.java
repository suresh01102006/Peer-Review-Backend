package com.peerreview.controller;

import com.peerreview.dto.request.AssignmentRequest;
import com.peerreview.dto.response.AssignmentResponse;
import com.peerreview.model.User;
import com.peerreview.service.AssignmentService;
import com.peerreview.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<AssignmentResponse>> getAll() {
        return ResponseEntity.ok(assignmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assignmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentRequest req,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        User creator = userService.getByEmail(userDetails.getUsername());
        return ResponseEntity.ok(assignmentService.create(req, creator));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignmentResponse> update(@PathVariable Long id,
                                                      @RequestBody AssignmentRequest req) {
        return ResponseEntity.ok(assignmentService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assignmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
