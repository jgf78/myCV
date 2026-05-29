package com.julian.cv.service;

import java.util.List;

import com.julian.cv.model.VisitRecord;
import com.julian.cv.model.WebVisitResponse;

public interface WebVisitService {
    void registerVisit(VisitRecord visitRecord);
    
    List<WebVisitResponse> getLastVisits(int limit);
}
