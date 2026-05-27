package com.julian.cv.service;

import com.julian.cv.model.GeoIpData;

public interface NotificationService {

    void sendVisitNotification(long visitNumber, String userAgent, String ip, long monthlyVisits, GeoIpData geo);
}
