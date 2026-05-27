package com.julian.cv.model;

public record VisitRecord(
        String path,
        String userAgent,
        String ip,
        String referer,
        String country,
        String city,
        String region
) {}
