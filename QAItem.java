package com.example.healthconsultapp;

public class QAItem {
    private int id;
    private String question;
    private String answer;

    public QAItem(int id, String question, String answer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
    }

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
}