package com.julian.cv.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.julian.cv.model.CountryVisitResponse;
import com.julian.cv.service.WebVisitService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DashboardApiController {

    private final WebVisitService visitService;

    @GetMapping("/admin/api/countries")
    public List<CountryVisitResponse> countries() {

        return visitService.getVisitsByCountry();
    }

    @GetMapping("/admin/api/countries/month")
    public List<CountryVisitResponse> monthlyCountries() {

        return visitService.getMonthlyVisitsByCountry();
    }
}