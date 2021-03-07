package com.example.quiz_app_mvvm.model

import com.google.firebase.firestore.DocumentId

data class QuestionsModel(
//        @DocumentId
//        val question_id: String = "",
        val question: String = "",
        val answer: String = "",
        val option_a: String = "",
        val option_b: String = "",
        val option_c: String = "",
        val option_d: String = "",
)
// public class QuestionsModel {
//    @DocumentId
//    private String question_id;
//    private String question, option_a, option_b, option_c, answer;
//    private int timer;
//
//    public QuestionsModel(){
//
//    }
//
//    public QuestionsModel(String question_id, String question, String option_a, String option_b, String option_c, String answer, int timer) {
//        this.question_id = question_id;
//        this.question = question;
//        this.option_a = option_a;
//        this.option_b = option_b;
//        this.option_c = option_c;
//        this.answer = answer;
//        this.timer = timer;
//    }
//
//    public String getQuestion_id() {
//        return question_id;
//    }
//
//    public void setQuestion_id(String question_id) {
//        this.question_id = question_id;
//    }
//
//    public String getQuestion() {
//        return question;
//    }
//
//    public void setQuestion(String question) {
//        this.question = question;
//    }
//
//    public String getOption_a() {
//        return option_a;
//    }
//
//    public void setOption_a(String option_a) {
//        this.option_a = option_a;
//    }
//
//    public String getOption_b() {
//        return option_b;
//    }
//
//    public void setOption_b(String option_b) {
//        this.option_b = option_b;
//    }
//
//    public String getOption_c() {
//        return option_c;
//    }
//
//    public void setOption_c(String option_c) {
//        this.option_c = option_c;
//    }
//
//    public String getAnswer() {
//        return answer;
//    }
//
//    public void setAnswer(String answer) {
//        this.answer = answer;
//    }
//
//    public int getTimer() {
//        return timer;
//    }
//
//    public void setTimer(int timer) {
//        this.timer = timer;
//    }
// }
