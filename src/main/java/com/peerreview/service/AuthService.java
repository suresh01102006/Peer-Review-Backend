package com.peerreview.service;

import com.peerreview.dto.request.LoginRequest;
import com.peerreview.dto.request.SignupRequest;
import com.peerreview.dto.response.AuthResponse;
import com.peerreview.dto.response.UserResponse;
import com.peerreview.model.User;
import com.peerreview.repository.UserRepository;
import com.peerreview.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registerd");
        }
        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();
        userRepository.save(user);
        String token = generateToken(req.getEmail());
        return AuthResponse.builder().token(token).user(toResponse(user)).build();
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepository.findByEmail(req.getEmail()).orElseThrow();
        String token = generateToken(req.getEmail());
        return AuthResponse.builder().token(token).user(toResponse(user)).build();
    }

    private String generateToken(String email) {
        UserDetails ud = userDetailsService.loadUserByUsername(email);
        return jwtUtils.generateToken(ud);
    }

    public static UserResponse toResponse(User u) {
        return UserResponse.builder()
                .id(u.getId()).name(u.getName()).email(u.getEmail())
                .role(u.getRole()).bio(u.getBio()).skills(u.getSkills())
                .avatarUrl(u.getAvatarUrl())
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                .build();
    }
}
