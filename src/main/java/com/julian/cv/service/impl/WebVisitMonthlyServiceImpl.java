package com.julian.cv.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.julian.cv.entity.WebVisitMonthly;
import com.julian.cv.repository.WebVisitMonthlyRepository;
import com.julian.cv.service.WebVisitMonthlyService;

@Service
public class WebVisitMonthlyServiceImpl implements WebVisitMonthlyService {

    private final WebVisitMonthlyRepository repository;

    public WebVisitMonthlyServiceImpl(WebVisitMonthlyRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void registerVisit() {

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        WebVisitMonthly counter = repository.findByYearAndMonth(year, month)
                .orElseGet(() -> WebVisitMonthly.builder()
                        .year(year)
                        .month(month)
                        .visits(0)
                        .build());

        counter.setVisits(counter.getVisits() + 1);

        repository.save(counter);
    }

    @Override
    public long getCurrentMonthVisits() {

        LocalDate now = LocalDate.now();

        return repository.findByYearAndMonth(now.getYear(), now.getMonthValue())
                .map(WebVisitMonthly::getVisits)
                .orElse(0L);
    }
}
