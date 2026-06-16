package com.julian.cv.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.julian.cv.model.Cv;
import com.julian.cv.model.Project;
import com.julian.cv.service.CvService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CvController {

    private final CvService cvService;

    @GetMapping("/")
    public String index(Model model, Locale locale) {

        Cv cv = cvService.getCv(locale);

        model.addAttribute("cv", cv);

        return "index";
    }
    
    
    @GetMapping("/projects")
    public String projects(Model model, Locale locale) {

        Project projects = cvService.getProjects(locale);

        model.addAttribute("projects", projects);

        return "projects";
    }
}