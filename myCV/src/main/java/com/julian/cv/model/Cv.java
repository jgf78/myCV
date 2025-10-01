package com.julian.cv.model;

import lombok.Data;

import java.util.List;

@Data
public class Cv {
    private String name;
    private String title;
    private String summary;
    private List<Experience> experience;
    private List<Education> education;
    private List<String> skills;
    private List<Language> languages;

    @Data
    public static class Experience {
        private String company;
        private String role;
        private String period;
        private List<String> details;
    }

    @Data
    public static class Education {
        private String degree;
        private String institution;
        private String period;
    }

    @Data
    public static class Language {
        private String name;
        private String level;
    }
}
