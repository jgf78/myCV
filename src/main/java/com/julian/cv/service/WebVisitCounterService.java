package com.julian.cv.service;

public interface WebVisitCounterService {

    long incrementAndGet();
    
    long getCurrentCount();
}