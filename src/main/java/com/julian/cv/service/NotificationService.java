package com.julian.cv.service;

public interface NotificationService {

    void sendVisitNotification(long visitNumber, String userAgent, String ip, long monthlyVisits);
}