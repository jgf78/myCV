package com.julian.cv.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.julian.cv.service.auth.JwtService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session) {

        if ("admin".equals(username) && "1234".equals(password)) {

            String token = jwtService.generateToken(username);

            session.setAttribute("JWT", token);

            return "redirect:/admin/dashboard";
        }

        return "redirect:/login?error=true";
    }

    public record LoginRequest(String username, String password) {}
}