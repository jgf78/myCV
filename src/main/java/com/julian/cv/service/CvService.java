package com.julian.cv.service;

import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.julian.cv.model.Cv;
import com.julian.cv.model.Project;

@Service
public class CvService {

    private final Cv cv;
    private final Project projects;

    public CvService() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new ClassPathResource("cv.yaml").getInputStream();
            this.cv = yaml.loadAs(inputStream, Cv.class);
            inputStream = new ClassPathResource("projects.yaml").getInputStream();
            this.projects = yaml.loadAs(inputStream, Project.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar yamls", e);
        }
    }

    public Cv getCv() {
        return cv;
    }
    
    public Project getProjects() {
        return projects;
    }
}
