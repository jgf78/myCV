package com.julian.cv.config;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.julian.cv.service.auth.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ rutas públicas
        if (isPublic(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔒 proteger admin
        if (path.startsWith("/admin")) {

            HttpSession session = request.getSession(false);

            if (session == null) {
                response.sendRedirect("/login");
                return;
            }

            String token = (String) session.getAttribute("JWT");

            if (token == null) {
                response.sendRedirect("/login");
                return;
            }

            try {
                // 🔓 validar token
                String username = jwtService.extractUsername(token);

                // ✅ CLAVE: marcar como autenticado en Spring Security
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

                // opcional: para Thymeleaf o uso manual
                request.setAttribute("user", username);

            } catch (Exception e) {

                SecurityContextHolder.clearContext();
                session.invalidate();

                response.sendRedirect("/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublic(String path) {

        return path.equals("/")
                || path.equals("/login")
                || path.equals("/auth/login")
                || path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")
                || path.startsWith("/webjars")
                || path.matches(".*\\.(png|jpg|jpeg|gif|svg|ico|css|js)$");
    }
}