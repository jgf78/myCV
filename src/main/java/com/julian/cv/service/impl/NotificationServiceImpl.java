package com.julian.cv.service.impl;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.julian.cv.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String URL =
            "http://jgf78.duckdns.org:8083/api/messages/send";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void sendVisitNotification(String userAgent, String ip) {

        String formattedDate = ZonedDateTime
                .now(ZoneId.of("Europe/Madrid"))
                .format(FORMATTER);

        String shortUA = simplifyUserAgent(userAgent);

        String message = "🔥 Nueva visita en la web\n\n"
                + "🕒 " + formattedDate + "\n"
                + "🌍 IP: " + ip + "\n"
                + "🧭 " + shortUA;

        Map<String, Object> body = Map.of(
                "message", message,
                "destination", "telegram",
                "destinationTelegram", "bot"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(URL, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String simplifyUserAgent(String ua) {

        if (ua == null) return "Unknown";

        String browser = "Unknown Browser";
        String os = "Unknown OS";

        if (ua.contains("Chrome")) browser = "Chrome";
        else if (ua.contains("Firefox")) browser = "Firefox";
        else if (ua.contains("Safari")) browser = "Safari";
        else if (ua.contains("Edge")) browser = "Edge";

        if (ua.contains("Windows")) os = "Windows";
        else if (ua.contains("Mac")) os = "Mac";
        else if (ua.contains("Linux")) os = "Linux";
        else if (ua.contains("Android")) os = "Android";
        else if (ua.contains("iPhone")) os = "iOS";

        return browser + " / " + os;
    }
}