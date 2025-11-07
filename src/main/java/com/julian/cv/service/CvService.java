package com.julian.cv.service;

import com.julian.cv.model.Cv;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

@Service
public class CvService {

    private final Cv cv;

    public CvService() {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = new ClassPathResource("cv.yaml").getInputStream();
            this.cv = yaml.loadAs(inputStream, Cv.class);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar cv.yaml", e);
        }
    }

    public Cv getCv() {
        return cv;
    }
}
