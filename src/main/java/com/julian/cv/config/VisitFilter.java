package com.julian.cv.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.julian.cv.model.VisitRecord;
import com.julian.cv.service.NotificationService;
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

    public VisitFilter(WebVisitService service,
                       NotificationService notificationService) {
        this.service = service;
        this.notificationService = notificationService;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        String path = req.getRequestURI();

        if (path.startsWith("/css") ||
            path.startsWith("/js") ||
            path.startsWith("/images") ||
            path.contains(".")) {

            chain.doFilter(request, response);
            return;
        }

        String userAgent = req.getHeader("User-Agent");
        String referer = req.getHeader("Referer");
        String ip = getClientIp(req);

        service.registerVisit(new VisitRecord(
                path,
                userAgent,
                ip,
                referer
        ));

        HttpSession session = req.getSession(true);

        if (session.getAttribute("VISITED") == null) {

            session.setAttribute("VISITED", true);

            notificationService.sendVisitNotification(userAgent, ip);
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
}