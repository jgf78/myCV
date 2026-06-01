package com.julian.cv.config;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.julian.cv.model.GeoIpData;
import com.julian.cv.model.VisitRecord;
import com.julian.cv.service.GeoIpService;
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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class VisitFilter implements Filter {

    private final WebVisitService service;
    private final NotificationService notificationService;
    private final WebVisitCounterService counterService;
    private final WebVisitMonthlyService counterMonthlyService;
    private final GeoIpService geoIpService;

    public VisitFilter(WebVisitService service,
                       NotificationService notificationService,
                       WebVisitCounterService counterService,
                       WebVisitMonthlyService counterMonthlyService,
                       GeoIpService geoIpService) {

        this.service = service;
        this.notificationService = notificationService;
        this.counterService = counterService;
        this.counterMonthlyService = counterMonthlyService;
        this.geoIpService = geoIpService;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // 🚫 ignorar recursos estáticos
        if (isStatic(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 🚫 NO registrar admin ni login
        if (isInternalPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 🤖 ignorar robots.txt
        if ("/robots.txt".equals(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 🤖 ignorar sitemap.xml
        if (path.equals("/sitemap.xml")) {
            chain.doFilter(request, response);
            return;
        }

        String userAgentRaw = req.getHeader("User-Agent");

        // 🤖 ignorar bots/scanners
        if (isBot(path, userAgentRaw)) {
            chain.doFilter(request, response);
            return;
        }

        String referer = req.getHeader("Referer");
        String ip = getClientIp(req);

        // 🌍 GeoIP SOLO para tráfico real
        GeoIpData geo = geoIpService.getGeoData(ip);

        String country = geo != null ? geo.country() : null;
        String city = geo != null ? geo.city() : null;
        String region = geo != null ? geo.region() : null;

        // 📦 guardar navegación real
        service.registerVisit(new VisitRecord(
                path,
                userAgentRaw,
                ip,
                referer,
                country,
                city,
                region
        ));

        // 🔐 contar SOLO 1 visita por sesión
        HttpSession session = req.getSession(true);

        if (session.getAttribute("VISIT_COUNTED") == null) {

            session.setAttribute("VISIT_COUNTED", true);

            // 📊 contador global
            long visitNumber = counterService.incrementAndGet();

            // 📅 contador mensual
            counterMonthlyService.registerVisit();

            long monthlyVisits =
                    counterMonthlyService.getCurrentMonthVisits();

            // 🔔 notificación
            notificationService.sendVisitNotification(
                    visitNumber,
                    simplifyUserAgent(userAgentRaw),
                    ip,
                    monthlyVisits,
                    geo
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

        if (ua == null) {
            return "Unknown";
        }

        if (ua.contains("Chrome")) {
            return "Chrome";
        }

        if (ua.contains("Firefox")) {
            return "Firefox";
        }

        if (ua.contains("Safari") && !ua.contains("Chrome")) {
            return "Safari";
        }

        if (ua.contains("Edge")) {
            return "Edge";
        }

        return "Other";
    }

    private boolean isStatic(String path) {

        return path.startsWith("/css")
                || path.startsWith("/js")
                || path.startsWith("/images")
                || path.startsWith("/webjars")
                || path.matches(".*\\.(png|jpg|jpeg|gif|svg|ico|css|js)$");
    }

    private boolean isBot(String path, String userAgent) {

        if (path == null) {
            return true;
        }

        String p = path.toLowerCase();

        // 🤖 scanners típicos
        if (p.contains("wp-admin")
                || p.contains("wp-login")
                || p.contains(".git")
                || p.contains(".env")
                || p.contains("phpinfo")
                || p.contains("xmlrpc")
                || p.contains("boaform")
                || p.contains("cgi-bin")
                || p.contains("vendor")
                || p.contains("alvin9999")
                || p.contains("struts")
                || p.contains("invoker")
                || p.contains("ping_isolation")
                || p.contains("proto_s")
                || p.contains("netskope")
                || p.contains("rbi-dialog")
                || p.contains("expired-tab")
                || p.contains("security.txt")) {

            return true;
        }

        // si no hay user-agent, no asumimos bot
        if (userAgent == null) {
            return false;
        }

        String ua = userAgent.toLowerCase();

        return ua.contains("bot")
                || ua.contains("crawler")
                || ua.contains("spider")
                || ua.contains("scanner")
                || ua.contains("curl")
                || ua.contains("wget")
                || ua.contains("python")
                || ua.contains("scrapy");
    }
    
    private boolean isInternalPath(String path) {

        if (path == null) return true;

        String p = path.toLowerCase();

        return p.startsWith("/admin")
                || p.startsWith("/auth")
                || p.equals("/login")
                || p.startsWith("/error");
    }
}