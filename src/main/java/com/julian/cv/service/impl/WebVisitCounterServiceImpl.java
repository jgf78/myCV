package com.julian.cv.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.julian.cv.entity.WebVisitCounter;
import com.julian.cv.repository.WebVisitCounterRepository;
import com.julian.cv.service.WebVisitCounterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebVisitCounterServiceImpl implements WebVisitCounterService {

    private final WebVisitCounterRepository repository;

    @Transactional
    @Override
    public synchronized long incrementAndGet() {

        WebVisitCounter counter = repository.findAll()
                .stream()
                .findFirst()
                .orElse(null);

        if (counter == null) {
            counter = WebVisitCounter.builder()
                    .totalVisits(1L)
                    .lastVisit(LocalDateTime.now())
                    .build();
        } else {
            counter.setTotalVisits(counter.getTotalVisits() + 1);
            counter.setLastVisit(LocalDateTime.now());
        }

        repository.save(counter);

        return counter.getTotalVisits();
    }
}
