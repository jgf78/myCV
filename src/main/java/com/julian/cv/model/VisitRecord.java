package com.julian.cv.model;

public record VisitRecord(
        String path,
        String userAgent,
        String ip,
        String referer
) {}
