package com.quizapp.models;

import java.util.List;

public class Question {
    private int id;
    private String questionText;
    private List<Option> options;

    public Question() {}

    public Question(int id, String questionText, List<Option> options) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
