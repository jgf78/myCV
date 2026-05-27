package com.julian.cv.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.julian.cv.model.VisitRecord;
import com.julian.cv.service.NotificationService;
import com.julian.cv.service.WebVisitCounterService;
import com.julian.cv.service.WebVisitService;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class VisitFilter implements Filter {

    private final WebVisitService service;
    private final NotificationService notificationService;
    private final WebVisitCounterService counterService;

    public VisitFilter(WebVisitService service,
                       NotificationService notificationService,
                       WebVisitCounterService counterService) {
        this.service = service;
        this.notificationService = notificationService;
        this.counterService = counterService;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String path = req.getRequestURI();

        // 🚫 ignorar recursos estáticos
        if (path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/images") ||
            path.contains(".")) {

            chain.doFilter(request, response);
            return;
        }

        String userAgentRaw = req.getHeader("User-Agent");
        String referer = req.getHeader("Referer");
        String ip = getClientIp(req);

        // 🧠 guardar visita
        service.registerVisit(new VisitRecord(
                path,
                userAgentRaw,
                ip,
                referer
        ));

        // 📊 incrementar contador global
        long visitNumber = counterService.incrementAndGet();

        // 🔐 sesión (solo 1 notificación por usuario)
        HttpSession session = req.getSession(true);

        if (session.getAttribute("VISITED") == null) {

            session.setAttribute("VISITED", true);

            // 🧼 User-Agent más limpio
            String userAgent = simplifyUserAgent(userAgentRaw);

            notificationService.sendVisitNotification(
                    visitNumber,
                    userAgent,
                    ip
            );
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0];
        }

        return request.getRemoteAddr();
    }

    private String simplifyUserAgent(String ua) {

        if (ua == null) return "Unknown";

        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Firefox")) return "Firefox";
        if (ua.contains("Safari") && !ua.contains("Chrome")) return "Safari";
        if (ua.contains("Edge")) return "Edge";

        return "Other";
    }
}