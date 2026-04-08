package com.peerreview.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotNull
    private Long projectId;
    @NotNull @Min(1) @Max(5)
    private Integer qualityRating;
    @NotNull @Min(1) @Max(5)
    private Integer creativityRating;
    @NotNull @Min(1) @Max(5)
    private Integer presentationRating;
    @NotBlank
    private String comment;
    private Boolean anonymous = false;
}
