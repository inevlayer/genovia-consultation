package com.genovia.consultation.domain.model;

import java.util.Objects;

public record Answer(String questionId, String answer) {
    public Answer(String questionId, String answer) {
        this.questionId = Objects.requireNonNull(questionId, "Question ID cannot be null");
        this.answer = Objects.requireNonNull(answer, "Answer cannot be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(questionId, answer1.questionId) &&
                Objects.equals(answer, answer1.answer);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "questionId='" + questionId + '\'' +
                ", answer='" + answer + '\'' +
                '}';
    }
}
