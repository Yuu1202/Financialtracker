package com.example.financialtracker;

import java.util.List;

public class ModelsResponse {
    private List<Model> models;

    public static class Model {
        private String name;
        private String modified_at;
        private long size;

        public String getName() {
            return name;
        }
    }

    public List<Model> getModels() {
        return models;
    }
}