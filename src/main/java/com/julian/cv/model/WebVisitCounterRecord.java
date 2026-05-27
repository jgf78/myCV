package com.julian.cv.model;

import java.time.LocalDateTime;

public record WebVisitCounterRecord(
        Long totalVisits,
        LocalDateTime lastVisit
) {}
