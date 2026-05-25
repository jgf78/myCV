package com.julian.cv.model;

import java.util.List;

import lombok.Data;

@Data
public class Project {

    private List<ProjectItem> projects;

    @Data
    public static class ProjectItem {
        private String name;
        private String description;
        private List<String> features;
        private List<String> technologies;
        private String github;
    }
}
