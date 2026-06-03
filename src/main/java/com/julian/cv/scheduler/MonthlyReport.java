package com.julian.cv.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.julian.cv.service.MonthlyReportService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MonthlyReport {

    private final MonthlyReportService monthlyReportService;

    // 📅 día 1 de cada mes a las 08:00
    @Scheduled(cron = "0 0 8 1 * *")
    //@Scheduled(cron = "*/10 * * * * *")
    public void sendMonthlyReport() {
        monthlyReportService.sendPreviousMonthReport();
    }
}