package com.julian.cv.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.julian.cv.entity.WebVisit;
import com.julian.cv.repository.WebVisitRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

        log.info("📅 Rango reporte: {} -> {}", start, end);

        List<WebVisit> visits = repository.findByVisitTimeBetween(start, end);

        long total = visits.size();

        log.info("📊 Visitas encontradas: {}", total);

        Map<String, Long> byCountry = visits.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getCountry() != null ? v.getCountry() : "Unknown",
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

        if (ua == null) {
            return "Unknown";
        }

        ua = ua.toLowerCase();

        // Tablet antes de Mobile porque algunos user-agent de tablet
        // contienen también la palabra "mobile".
        if (ua.contains("tablet")
                || ua.contains("ipad")
                || ua.contains("android") && !ua.contains("mobile")) {
            return "Tablet";
        }

        if (ua.contains("mobile")
                || ua.contains("iphone")
                || ua.contains("ipod")) {
            return "Mobile";
        }

        return "Desktop";
    }

    private String buildMessage(
            long total,
            Map<String, Long> byCountry,
            Map<String, Long> byDevice,
            LocalDateTime start) {

        String monthEs = start.getMonth()
                .getDisplayName(
                        TextStyle.FULL,
                        new Locale("es", "ES")
                );

        long maxCountry = byCountry.values().stream()
                .max(Long::compareTo)
                .orElse(1L);

        String countriesHtml = byCountry.entrySet().stream()
                .sorted(
                        Map.Entry.<String, Long>comparingByValue()
                                .reversed()
                )
                .map(entry -> {

                    String country = entry.getKey();
                    long visits = entry.getValue();

                    double percentage = total > 0
                            ? (visits * 100.0) / total
                            : 0;

                    int barWidth = (int) Math.round(
                            (visits * 100.0) / maxCountry
                    );

                    return """
                            <tr>
                                <td style="padding:8px 0;color:#e5e7eb;font-size:14px;width:45px;">
                                    %s
                                </td>

                                <td style="padding:8px 10px;">
                                    <div style="background:#374151;border-radius:6px;height:8px;width:100%%;">
                                        <div style="background:#38bdf8;border-radius:6px;height:8px;width:%d%%;"></div>
                                    </div>
                                </td>

                                <td style="padding:8px 0;color:#ffffff;font-size:14px;text-align:right;white-space:nowrap;">
                                    %d <span style="color:#9ca3af;">(%.1f%%)</span>
                                </td>
                            </tr>
                            """.formatted(
                            country,
                            barWidth,
                            visits,
                            percentage
                    );
                })
                .collect(Collectors.joining());

        long desktop = byDevice.getOrDefault("Desktop", 0L);
        long mobile = byDevice.getOrDefault("Mobile", 0L);
        long tablet = byDevice.getOrDefault("Tablet", 0L);
        long unknown = byDevice.getOrDefault("Unknown", 0L);

        double desktopPercentage = total > 0
                ? desktop * 100.0 / total
                : 0;

        double mobilePercentage = total > 0
                ? mobile * 100.0 / total
                : 0;

        double tabletPercentage = total > 0
                ? tablet * 100.0 / total
                : 0;

        double unknownPercentage = total > 0
                ? unknown * 100.0 / total
                : 0;

        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reporte mensual MyCV</title>
                </head>

                <body style="
                    margin:0;
                    padding:0;
                    background:#0f172a;
                    font-family:Arial,Helvetica,sans-serif;
                    color:#e5e7eb;
                ">

                    <div style="
                        max-width:700px;
                        margin:0 auto;
                        padding:30px 15px;
                    ">

                        <!-- HEADER -->

                        <div style="
                            background:#111827;
                            border:1px solid #1f2937;
                            border-radius:16px;
                            padding:30px;
                            text-align:center;
                            margin-bottom:20px;
                        ">

                            <div style="
                                font-size:42px;
                                margin-bottom:10px;
                            ">📊</div>

                            <h1 style="
                                margin:0;
                                color:#ffffff;
                                font-size:28px;
                            ">
                                Reporte mensual
                            </h1>

                            <p style="
                                margin:8px 0 0;
                                color:#38bdf8;
                                font-size:18px;
                                text-transform:capitalize;
                            ">
                                %s
                            </p>

                        </div>


                        <!-- TOTAL VISITS -->

                        <div style="
                            background:#111827;
                            border:1px solid #1f2937;
                            border-radius:16px;
                            padding:30px;
                            text-align:center;
                            margin-bottom:20px;
                        ">

                            <div style="
                                font-size:14px;
                                color:#9ca3af;
                                text-transform:uppercase;
                                letter-spacing:1px;
                            ">
                                Visitas totales
                            </div>

                            <div style="
                                font-size:48px;
                                font-weight:bold;
                                color:#38bdf8;
                                margin-top:8px;
                            ">
                                %,d
                            </div>

                            <div style="
                                color:#6b7280;
                                font-size:13px;
                                margin-top:5px;
                            ">
                                visitas registradas durante el mes
                            </div>

                        </div>


                        <!-- COUNTRIES -->

                        <div style="
                            background:#111827;
                            border:1px solid #1f2937;
                            border-radius:16px;
                            padding:25px;
                            margin-bottom:20px;
                        ">

                            <h2 style="
                                margin:0 0 20px;
                                color:#ffffff;
                                font-size:20px;
                            ">
                                🌍 Visitas por país
                            </h2>

                            <table style="
                                width:100%%;
                                border-collapse:collapse;
                            ">

                                %s

                            </table>

                        </div>


                        <!-- DEVICES -->

                        <div style="
                            background:#111827;
                            border:1px solid #1f2937;
                            border-radius:16px;
                            padding:25px;
                            margin-bottom:20px;
                        ">

                            <h2 style="
                                margin:0 0 20px;
                                color:#ffffff;
                                font-size:20px;
                            ">
                                📱 Dispositivos
                            </h2>


                            <!-- DESKTOP -->

                            <div style="
                                margin-bottom:18px;
                            ">

                                <div style="
                                    display:flex;
                                    justify-content:space-between;
                                    margin-bottom:6px;
                                ">

                                    <span style="color:#e5e7eb;">
                                        🖥️ Desktop
                                    </span>

                                    <span style="color:#9ca3af;">
                                        %,d (%.1f%%)
                                    </span>

                                </div>

                                <div style="
                                    background:#374151;
                                    height:10px;
                                    border-radius:6px;
                                    width:100%%;
                                ">

                                    <div style="
                                        background:#38bdf8;
                                        height:10px;
                                        width:%.1f%%;
                                        border-radius:6px;
                                    "></div>

                                </div>

                            </div>


                            <!-- MOBILE -->

                            <div style="
                                margin-bottom:18px;
                            ">

                                <div style="
                                    display:flex;
                                    justify-content:space-between;
                                    margin-bottom:6px;
                                ">

                                    <span style="color:#e5e7eb;">
                                        📱 Mobile
                                    </span>

                                    <span style="color:#9ca3af;">
                                        %,d (%.1f%%)
                                    </span>

                                </div>

                                <div style="
                                    background:#374151;
                                    height:10px;
                                    border-radius:6px;
                                    width:100%%;
                                ">

                                    <div style="
                                        background:#22c55e;
                                        height:10px;
                                        width:%.1f%%;
                                        border-radius:6px;
                                    "></div>

                                </div>

                            </div>


                            <!-- TABLET -->

                            <div style="
                                margin-bottom:18px;
                            ">

                                <div style="
                                    display:flex;
                                    justify-content:space-between;
                                    margin-bottom:6px;
                                ">

                                    <span style="color:#e5e7eb;">
                                        📱 Tablet
                                    </span>

                                    <span style="color:#9ca3af;">
                                        %,d (%.1f%%)
                                    </span>

                                </div>

                                <div style="
                                    background:#374151;
                                    height:10px;
                                    border-radius:6px;
                                    width:100%%;
                                ">

                                    <div style="
                                        background:#f59e0b;
                                        height:10px;
                                        width:%.1f%%;
                                        border-radius:6px;
                                    "></div>

                                </div>

                            </div>


                            <!-- UNKNOWN -->

                            <div>

                                <div style="
                                    display:flex;
                                    justify-content:space-between;
                                    margin-bottom:6px;
                                ">

                                    <span style="color:#e5e7eb;">
                                        ❓ Unknown
                                    </span>

                                    <span style="color:#9ca3af;">
                                        %,d (%.1f%%)
                                    </span>

                                </div>

                                <div style="
                                    background:#374151;
                                    height:10px;
                                    border-radius:6px;
                                    width:100%%;
                                ">

                                    <div style="
                                        background:#a855f7;
                                        height:10px;
                                        width:%.1f%%;
                                        border-radius:6px;
                                    "></div>

                                </div>

                            </div>

                        </div>


                        <!-- FOOTER -->

                        <div style="
                            text-align:center;
                            padding:10px;
                            color:#6b7280;
                            font-size:12px;
                        ">
                            Informe generado automáticamente por MyCV
                        </div>

                    </div>

                </body>
                </html>
                """.formatted(
                monthEs,
                total,
                countriesHtml,
                desktop,
                desktopPercentage,
                desktopPercentage,
                mobile,
                mobilePercentage,
                mobilePercentage,
                tablet,
                tabletPercentage,
                tabletPercentage,
                unknown,
                unknownPercentage,
                unknownPercentage
        );
    }
}