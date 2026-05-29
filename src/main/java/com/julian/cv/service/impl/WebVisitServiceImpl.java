package com.julian.cv.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.julian.cv.entity.WebVisit;
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
}
