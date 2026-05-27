package com.julian.cv.controller.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.julian.cv.service.auth.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (!"admin".equals(request.username()) || !"1234".equals(request.password())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtService.generateToken(request.username());

        return ResponseEntity.ok(Map.of(
                "token", token
        ));
    }

    public record LoginRequest(String username, String password) {}
}