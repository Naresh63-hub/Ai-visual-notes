package com.visualnotes.controller;

import com.visualnotes.dto.AuthResponse;
import com.visualnotes.dto.LoginRequest;
import com.visualnotes.dto.RegisterRequest;
import com.visualnotes.dto.UserDto;
import com.visualnotes.entity.User;
import com.visualnotes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        User user = authService.getCurrentUser();
        if (user == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(authService.mapToDto(user));
    }
}
