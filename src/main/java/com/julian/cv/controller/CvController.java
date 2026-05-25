package com.julian.cv.controller;

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
    public String index(Model model) {
        Cv cv = cvService.getCv();
        model.addAttribute("cv", cv);
        return "index";
    }
    
    @GetMapping("/projects")
    public String projects(Model model) {
        Project projects = cvService.getProjects();
        model.addAttribute("projects", projects);
        return "projects";
    }
}
