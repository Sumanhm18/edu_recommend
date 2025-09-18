package com.education.education.dto;

import java.util.List;

public class QuizSubmissionDto {

    private Long quizId;
    private List<QuizAnswerDto> answers;

    public QuizSubmissionDto() {
    }

    public QuizSubmissionDto(Long quizId, List<QuizAnswerDto> answers) {
        this.quizId = quizId;
        this.answers = answers;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public List<QuizAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuizAnswerDto> answers) {
        this.answers = answers;
    }

    public static class QuizAnswerDto {
        private Integer questionId;
        private String selectedOption; // A, B, C, D
        private Integer timeSpent; // in seconds

        public QuizAnswerDto() {
        }

        public QuizAnswerDto(Integer questionId, String selectedOption, Integer timeSpent) {
            this.questionId = questionId;
            this.selectedOption = selectedOption;
            this.timeSpent = timeSpent;
        }

        public Integer getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }

        public Integer getTimeSpent() {
            return timeSpent;
        }

        public void setTimeSpent(Integer timeSpent) {
            this.timeSpent = timeSpent;
        }
    }
}