package com.julian.cv.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.julian.cv.entity.WebVisit;
import com.julian.cv.repository.WebVisitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyReportService {

    private final WebVisitRepository repository;
    private final NotificationService notificationService;

    public void sendPreviousMonthReport() {

        LocalDateTime start = LocalDate.now()
                .minusMonths(1)
                .withDayOfMonth(1)
                .atStartOfDay();

        LocalDateTime end = LocalDate.now()
                .withDayOfMonth(1)
                .atStartOfDay();

        List<WebVisit> visits = repository.findByVisitTimeBetween(start, end);

        long total = visits.size();

        Map<String, Long> byCountry = visits.stream()
                .collect(Collectors.groupingBy(
                        WebVisit::getCountry,
                        Collectors.counting()
                ));

        Map<String, Long> byDevice = visits.stream()
                .collect(Collectors.groupingBy(
                        this::detectDevice,
                        Collectors.counting()
                ));

        String message = buildMessage(total, byCountry, byDevice, start);

        notificationService.sendMonthlyReport(message);
    }

    private String detectDevice(WebVisit v) {

        String ua = v.getUserAgent();
        if (ua == null) return "Unknown";

        ua = ua.toLowerCase();

        if (ua.contains("mobile")) return "Mobile";
        if (ua.contains("tablet")) return "Tablet";
        return "Desktop";
    }

    private String buildMessage(long total,
                                Map<String, Long> byCountry,
                                Map<String, Long> byDevice,
                                LocalDateTime start) {

        return """
        📊 Reporte mensual - %s

        🔥 Visitas totales: %d

        🌍 Por país:
        %s

        📱 Dispositivos:
        %s
        """.formatted(
                start.getMonth(),
                total,
                formatMap(byCountry),
                formatMap(byDevice)
        );
    }

    private String formatMap(Map<String, Long> map) {

        return map.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> " - " + e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("\n"));
    }
}
