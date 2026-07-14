package com.Youtube.hospitalManagement.controller;

import com.Youtube.hospitalManagement.dto.LoginRequestDto;
import com.Youtube.hospitalManagement.dto.LoginResponseDto;
import com.Youtube.hospitalManagement.dto.SignupResponseDto;
import com.Youtube.hospitalManagement.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// AuthController handles all authentication-related endpoints
// These routes are PUBLIC — no JWT token required
// Defined in WebSecurityConfig: .requestMatchers("/auth/**").permitAll()

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /auth/login
    // Body: { "username": "raj", "password": "1234" }
    // Returns: { "jwt": "eyJhbG...", "userId": 1 }
    // Client stores this JWT and sends it with every future request
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    // POST /auth/signup
    // Body: { "username": "raj", "password": "1234" }
    // Returns: { "id": 1, "username": "raj" }
    // Creates new user with PATIENT role by default
    // Fixed: parameter was named "loginRequestDto" but used as "signupRequestDto"
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(
            @RequestBody LoginRequestDto signupRequestDto) {
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }
}