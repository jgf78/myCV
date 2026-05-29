package com.julian.cv.model;

import java.time.LocalDateTime;

public record WebVisitResponse(
        String path,
        String ip,
        String country,
        String city,
        String region,
        String userAgent,
        String referer,
        LocalDateTime visitTime
) {}