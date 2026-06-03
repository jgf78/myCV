package com.julian.cv.service;

import java.util.List;

import com.julian.cv.model.CountryVisitResponse;
import com.julian.cv.model.DailyVisitResponse;
import com.julian.cv.model.DeviceStatsResponse;
import com.julian.cv.model.VisitRecord;
import com.julian.cv.model.WebVisitResponse;

public interface WebVisitService {
    void registerVisit(VisitRecord visitRecord);
    
    List<WebVisitResponse> getLastVisits(int limit);

    List<CountryVisitResponse> getVisitsByCountry();

    List<CountryVisitResponse> getMonthlyVisitsByCountry();
    
    List<DeviceStatsResponse> getDeviceStats();

    List<DailyVisitResponse> getDailyVisitsCurrentMonth();
}
