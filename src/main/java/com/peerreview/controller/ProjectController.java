package com.peerreview.controller;

import com.peerreview.dto.request.ProjectRequest;
import com.peerreview.dto.response.ProjectResponse;
import com.peerreview.model.User;
import com.peerreview.model.enums.ProjectStatus;
import com.peerreview.service.ProjectService;
import com.peerreview.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> search(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(projectService.search(search, tag, status));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponse>> myProjects(@AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(projectService.getMyProjects(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getById(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<ProjectResponse> submit(
            @RequestPart("project") ProjectRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserDetails ud) {
        User user = userService.getByEmail(ud.getUsername());
        return ResponseEntity.ok(projectService.submit(req, file, user));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponse> updateStatus(@PathVariable Long id,
                                                         @RequestBody Map<String, String> body) {
        ProjectStatus status = ProjectStatus.valueOf(body.get("status").toUpperCase());
        return ResponseEntity.ok(projectService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
