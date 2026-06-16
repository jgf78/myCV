package com.julian.cv.service;

import java.io.InputStream;
import java.util.Locale;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.julian.cv.model.Cv;
import com.julian.cv.model.Project;

@Service
public class CvService {

    private static final String DEFAULT_LANG = "es";

    public Cv getCv(Locale locale) {
        String lang = getLanguage(locale);

        try {
            return loadYaml("cv_" + lang + ".yaml", Cv.class);

        } catch (Exception e) {

            // fallback español si no existe el idioma
            return loadYaml("cv_" + DEFAULT_LANG + ".yaml", Cv.class);
        }
    }


    public Project getProjects(Locale locale) {

        String lang = getLanguage(locale);

        try {
            return loadYaml("projects_" + lang + ".yaml", Project.class);

        } catch (Exception e) {

            // fallback español
            return loadYaml("projects_" + DEFAULT_LANG + ".yaml", Project.class);
        }
    }


    private String getLanguage(Locale locale) {

        if (locale == null) {
            return DEFAULT_LANG;
        }

        String lang = locale.getLanguage();

        if ("en".equals(lang)) {
            return "en";
        }

        return DEFAULT_LANG;
    }


    private <T> T loadYaml(String fileName, Class<T> clazz) {

        try {

            Yaml yaml = new Yaml();

            InputStream inputStream =
                    new ClassPathResource(fileName).getInputStream();

            return yaml.loadAs(inputStream, clazz);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error cargando YAML: " + fileName,
                    e
            );
        }
    }
}