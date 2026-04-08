package com.peerreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PeerReviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeerReviewApplication.class, args);
    }
}
