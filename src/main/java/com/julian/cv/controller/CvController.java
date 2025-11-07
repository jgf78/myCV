package com.julian.cv.controller;

import com.julian.cv.service.CvService;
import com.julian.cv.model.Cv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    @GetMapping("/")
    public String index(Model model) {
        Cv cv = cvService.getCv();
        model.addAttribute("cv", cv);
        return "index";
    }
}
