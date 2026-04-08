package com.peerreview.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String bio;
    private String skills;
    private String avatarUrl;
}
