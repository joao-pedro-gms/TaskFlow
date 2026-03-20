package com.taskflow.service;

import com.taskflow.dto.request.LoginRequest;
import com.taskflow.dto.request.RegisterRequest;
import com.taskflow.dto.response.AuthResponse;
import com.taskflow.entity.mongo.ActivityLog;
import com.taskflow.entity.mongo.UserPreference;
import com.taskflow.entity.postgres.Category;
import com.taskflow.entity.postgres.User;
import com.taskflow.exception.BadRequestException;
import com.taskflow.repository.mongo.ActivityLogRepository;
import com.taskflow.repository.mongo.UserPreferenceRepository;
import com.taskflow.repository.postgres.CategoryRepository;
import com.taskflow.repository.postgres.UserRepository;
import com.taskflow.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final ActivityLogRepository activityLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private static final List<String[]> DEFAULT_CATEGORIES = List.of(
            new String[]{"Work", "#3b82f6", "briefcase"},
            new String[]{"Personal", "#8b5cf6", "user"},
            new String[]{"Health", "#10b981", "heart"},
            new String[]{"Learning", "#f59e0b", "book-open"},
            new String[]{"Finance", "#06b6d4", "dollar-sign"},
            new String[]{"Home", "#ef4444", "home"}
    );

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .build();
        user = userRepository.save(user);

        seedDefaultCategories(user);
        createDefaultPreferences(user.getId());

        activityLogRepository.save(ActivityLog.of(user.getId(), "USER_REGISTERED", Map.of("email", user.getEmail())));
        log.info("New user registered: {}", user.getEmail());

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        activityLogRepository.save(ActivityLog.of(user.getId(), "LOGIN", Map.of("email", user.getEmail())));
        return buildAuthResponse(user);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        String email = jwtTokenProvider.extractEmail(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities(java.util.Collections.emptyList())
                        .build();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(900)
                .user(AuthResponse.UserDTO.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .displayName(user.getDisplayName())
                        .build())
                .build();
    }

    private void seedDefaultCategories(User user) {
        for (String[] cat : DEFAULT_CATEGORIES) {
            Category category = Category.builder()
                    .user(user)
                    .name(cat[0])
                    .colorHex(cat[1])
                    .iconName(cat[2])
                    .build();
            categoryRepository.save(category);
        }
    }

    private void createDefaultPreferences(Long userId) {
        UserPreference pref = UserPreference.builder()
                .userId(userId)
                .build();
        userPreferenceRepository.save(pref);
    }
}
