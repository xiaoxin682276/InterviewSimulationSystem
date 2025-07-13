package com.interview.model;

import java.util.List;

public class InterviewRequest {
    private String position;
    private List<String> answers;
    private List<String> questions;

    // 构造函数
    public InterviewRequest() {}

    public InterviewRequest(String position, List<String> answers, List<String> questions) {
        this.position = position;
        this.answers = answers;
        this.questions = questions;
    }

    // Getter和Setter方法
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
} 