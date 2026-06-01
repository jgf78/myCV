package com.julian.cv.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.julian.cv.service.WebVisitCounterService;
import com.julian.cv.service.WebVisitService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final WebVisitCounterService counterService;
    private final WebVisitService visitService;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("totalVisits", counterService.getCurrentCount());

        model.addAttribute("lastVisits", visitService.getLastVisits(10));
        
        // 🌍 gráfico histórico por países
        model.addAttribute(
                "countryVisits",
                visitService.getVisitsByCountry());

        // 📅 gráfico mensual por países
        model.addAttribute(
                "monthlyCountryVisits",
                visitService.getMonthlyVisitsByCountry());

        return "dashboard";
    }
}