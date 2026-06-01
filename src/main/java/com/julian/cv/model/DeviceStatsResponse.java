package com.julian.cv.model;

public record DeviceStatsResponse(
        String device,
        long visits
) {}