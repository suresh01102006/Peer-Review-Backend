package com.peerreview.controller;

import com.peerreview.dto.response.ProjectResponse;
import com.peerreview.service.AdminService;
import com.peerreview.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/students")
    public ResponseEntity<List<Map<String, Object>>> getStudentPerformance() {
        return ResponseEntity.ok(adminService.getStudentPerformance());
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<ProjectResponse>> getSubmissions() {
        return ResponseEntity.ok(adminService.getAllSubmissions());
    }

    @GetMapping("/export/report")
    public ResponseEntity<byte[]> exportReport() {
        String csv = adminService.exportReportCsv();
        byte[] bytes = csv.getBytes();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
