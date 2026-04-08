package com.peerreview.service;

import com.peerreview.dto.request.AssignmentRequest;
import com.peerreview.dto.response.AssignmentResponse;
import com.peerreview.exception.ResourceNotFoundException;
import com.peerreview.model.Assignment;
import com.peerreview.model.User;
import com.peerreview.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentResponse create(AssignmentRequest req, User creator) {
        Assignment a = Assignment.builder()
                .title(req.getTitle()).description(req.getDescription())
                .deadline(req.getDeadline()).category(req.getCategory())
                .createdBy(creator).build();
        return toResponse(assignmentRepository.save(a));
    }

    public List<AssignmentResponse> findAll() {
        return assignmentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AssignmentResponse findById(Long id) {
        return toResponse(getById(id));
    }

    public Assignment getById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found: " + id));
    }

    public AssignmentResponse update(Long id, AssignmentRequest req) {
        Assignment a = getById(id);
        if (req.getTitle() != null) a.setTitle(req.getTitle());
        if (req.getDescription() != null) a.setDescription(req.getDescription());
        if (req.getDeadline() != null) a.setDeadline(req.getDeadline());
        if (req.getCategory() != null) a.setCategory(req.getCategory());
        return toResponse(assignmentRepository.save(a));
    }

    public void delete(Long id) {
        assignmentRepository.deleteById(id);
    }

    public AssignmentResponse toResponse(Assignment a) {
        return AssignmentResponse.builder()
                .id(a.getId()).title(a.getTitle()).description(a.getDescription())
                .deadline(a.getDeadline() != null ? a.getDeadline().toString() : null)
                .category(a.getCategory())
                .createdBy(a.getCreatedBy() != null ? AuthService.toResponse(a.getCreatedBy()) : null)
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().toString() : null)
                .build();
    }
}
