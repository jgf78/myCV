package com.julian.cv.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.julian.cv.model.VisitRecord;
import com.julian.cv.service.NotificationService;
import com.julian.cv.service.WebVisitCounterService;
import com.julian.cv.service.WebVisitMonthlyService;
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
    private final WebVisitMonthlyService counterMonthlyService;

    public VisitFilter(WebVisitService service,
                       NotificationService notificationService,
                       WebVisitCounterService counterService,
                       WebVisitMonthlyService counterMonthlyService) {
        this.service = service;
        this.notificationService = notificationService;
        this.counterService = counterService;
        this.counterMonthlyService = counterMonthlyService;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String path = req.getRequestURI();

        // 🚫 ignorar estáticos
        if (isStatic(path)) {
            chain.doFilter(request, response);
            return;  
        }
        
        String userAgentRaw = req.getHeader("User-Agent");
        String referer = req.getHeader("Referer");
        String ip = getClientIp(req);

        // 📦 guardar visita detallada (LOG)
        service.registerVisit(new VisitRecord(
                path,
                userAgentRaw,
                ip,
                referer
        ));

        // 📊 contador global SIEMPRE
        long visitNumber = counterService.incrementAndGet();

        // 📅 contador mensual SIEMPRE
        counterMonthlyService.registerVisit();

        // 🔐 notificación solo primera vez por sesión
        HttpSession session = req.getSession(true);

        if (session.getAttribute("VISITED") == null) {

            session.setAttribute("VISITED", true);

            long monthlyVisits = counterMonthlyService.getCurrentMonthVisits();

            notificationService.sendVisitNotification(
                    visitNumber,
                    simplifyUserAgent(userAgentRaw),
                    ip,
                    monthlyVisits
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
    
    private boolean isStatic(String path) {
        return path.startsWith("/css")
            || path.startsWith("/js")
            || path.startsWith("/images")
            || path.startsWith("/webjars")
            || path.matches(".*\\.(png|jpg|jpeg|gif|svg|ico|css|js)$");
    }
}