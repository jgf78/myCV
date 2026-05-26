package com.julian.cv.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.julian.cv.entity.WebVisit;
import com.julian.cv.model.VisitRecord;
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
                .build();

        repository.save(visit);
    }
}
