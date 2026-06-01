package com.julian.cv.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.julian.cv.entity.WebVisit;
import com.julian.cv.model.CountryVisitResponse;
import com.julian.cv.model.DeviceStatsResponse;
import com.julian.cv.model.VisitRecord;
import com.julian.cv.model.WebVisitResponse;
import com.julian.cv.repository.WebVisitRepository;
import com.julian.cv.service.WebVisitService;

@Service
public class WebVisitServiceImpl implements WebVisitService {

    private final WebVisitRepository repository;

    public WebVisitServiceImpl(WebVisitRepository repository) {
        this.repository = repository;
    }

    @Override
    public void registerVisit(VisitRecord visitRecord) {

        WebVisit visit = WebVisit.builder()
                .path(visitRecord.path())
                .userAgent(visitRecord.userAgent())
                .ip(visitRecord.ip())
                .referer(visitRecord.referer())
                .visitTime(LocalDateTime.now())
                .city(visitRecord.city())
                .country(visitRecord.country())
                .region(visitRecord.region())
                .build();

        repository.save(visit);
    }

    @Override
    public List<WebVisitResponse> getLastVisits(int limit) {

        return repository.findAllByOrderByIdDesc(
                        PageRequest.of(0, limit)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<CountryVisitResponse> getVisitsByCountry() {

        return repository.findVisitsByCountry()
                .stream()
                .map(country -> new CountryVisitResponse(
                        country.getCountry(),
                        country.getVisits()))
                .toList();
    }

    @Override
    public List<CountryVisitResponse> getMonthlyVisitsByCountry() {

        LocalDate now = LocalDate.now();

        return repository.findMonthlyVisitsByCountry(
                        now.getYear(),
                        now.getMonthValue())
                .stream()
                .map(country -> new CountryVisitResponse(
                        country.getCountry(),
                        country.getVisits()))
                .toList();
    }

    private WebVisitResponse toResponse(WebVisit v) {

        return new WebVisitResponse(
                v.getPath(),
                v.getIp(),
                v.getCountry(),
                v.getCity(),
                v.getRegion(),
                v.getUserAgent(),
                v.getReferer(),
                v.getVisitTime()
        );
    }

    @Override
    public List<DeviceStatsResponse> getDeviceStats() {

        List<WebVisit> visits = repository.findAll();

        long desktop = 0;
        long mobile = 0;
        long tablet = 0;

        for (WebVisit v : visits) {

            String ua = v.getUserAgent();
            String device = detectDevice(ua);

            switch (device) {
                case "Desktop" -> desktop++;
                case "Mobile" -> mobile++;
                case "Tablet" -> tablet++;
            }
        }

        return List.of(
                new DeviceStatsResponse("Desktop", desktop),
                new DeviceStatsResponse("Mobile", mobile),
                new DeviceStatsResponse("Tablet", tablet)
        );
    }
    
    private String detectDevice(String ua) {

        if (ua == null) return "Desktop";

        ua = ua.toLowerCase();

        // 📱 Mobile
        if (ua.contains("iphone") ||
            ua.contains("android") ||
            ua.contains("mobile")) {
            return "Mobile";
        }

        // 📟 Tablet
        if (ua.contains("ipad") ||
            ua.contains("tablet")) {
            return "Tablet";
        }

        // 💻 Default
        return "Desktop";
    }
}