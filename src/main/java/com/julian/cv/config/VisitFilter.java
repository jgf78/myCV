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

        String path = req.getRequestURI();
        String userAgentRaw = req.getHeader("User-Agent");

        if (shouldIgnore(path, userAgentRaw)) {
            chain.doFilter(request, response);
            return;
        }

        String referer = req.getHeader("Referer");
        String ip = getClientIp(req);

        GeoIpData geo = geoIpService.getGeoData(ip);

        String country = geo != null ? geo.country() : null;
        String city = geo != null ? geo.city() : null;
        String region = geo != null ? geo.region() : null;

        service.registerVisit(new VisitRecord(
                path,
                userAgentRaw,
                ip,
                referer,
                country,
                city,
                region
        ));

        HttpSession session = req.getSession(true);

        if (session.getAttribute("VISIT_COUNTED") == null) {

            session.setAttribute("VISIT_COUNTED", true);

            long visitNumber = counterService.incrementAndGet();

            counterMonthlyService.registerVisit();

            long monthlyVisits =
                    counterMonthlyService.getCurrentMonthVisits();

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

    private boolean shouldIgnore(String path, String userAgent) {

        if (path == null) {
            return true;
        }

        String p = path.toLowerCase();

        return isStatic(p)
                || isInternalPath(p)
                || "/robots.txt".equals(p)
                || "/sitemap.xml".equals(p)
                || p.startsWith("/.")
                || isScannerPath(p)
                || isScannerUserAgent(userAgent);
    }

    private String getClientIp(HttpServletRequest request) {

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
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

    private boolean isInternalPath(String path) {

        return path.startsWith("/admin")
                || path.startsWith("/auth")
                || path.equals("/login")
                || path.startsWith("/error");
    }

    private boolean isScannerUserAgent(String userAgent) {

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
                || ua.contains("scrapy")
                || ua.contains("cortex-xpanse")
                || ua.contains("palo alto networks");
    }

    private boolean isScannerPath(String p) {

        return

                // PHP
                p.endsWith(".php")
                || p.contains("phpinfo")
                || p.contains("server-status")
                || p.contains("server-info")

                // secretos
                || p.contains(".env")
                || p.contains(".git")
                || p.contains("credentials.json")
                || p.contains("service-account")
                || p.contains("firebase")
                || p.contains("google-key")
                || p.contains("gcp-key")
                || p.contains("application_default_credentials")

                // WordPress
                || p.startsWith("/wp")
                || p.contains("wp-admin")
                || p.contains("wp-login")
                || p.contains("xmlrpc")

                // Magento
                || p.contains("magento_version")
                || p.startsWith("/rest/v1/store")

                // Joomla
                || p.startsWith("/modules/")
                || p.contains("mod_login.xml")

                // scanners conocidos
                || p.contains("cgi-bin")
                || p.contains("boaform")
                || p.contains("struts")
                || p.contains("invoker")
                || p.contains("vendor")
                || p.contains("netskope")
                || p.contains("security.txt")

                // variables de entorno
                || p.contains("_environment")

                // fuzzing
                || p.matches("^/[a-z0-9]{8,}$")
                || p.matches("^/[a-z0-9]{1,3}(-[a-z0-9]{1,3})+$");
    }
}