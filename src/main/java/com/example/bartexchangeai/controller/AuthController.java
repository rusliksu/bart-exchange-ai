package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.auth.AuthResponse;
import com.example.bartexchangeai.dto.auth.LoginRequest;
import com.example.bartexchangeai.dto.auth.RegisterRequest;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.model.user.Role;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @Transactional
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username уже занят: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email уже занят: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setRating(0.0f);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Username или email уже заняты");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getUsername(), user.getRole().name()));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с username=" + request.getUsername() + " не найден"));
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
