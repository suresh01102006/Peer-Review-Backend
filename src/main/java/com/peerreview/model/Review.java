package com.peerreview.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(nullable = false)
    private Integer qualityRating;

    @Column(nullable = false)
    private Integer creativityRating;

    @Column(nullable = false)
    private Integer presentationRating;

    private Double overallRating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Builder.Default
    private Boolean anonymous = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    public void calculateOverallRating() {
        this.overallRating = (qualityRating + creativityRating + presentationRating) / 3.0;
    }
}
