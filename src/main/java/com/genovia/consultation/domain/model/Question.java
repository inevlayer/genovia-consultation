package com.genovia.consultation.domain.model;

import java.util.List;
import java.util.Objects;

public record Question(String id, String text, QuestionType type, boolean required, String disqualifyingAnswer, List<String> subPoints) {
    public Question(String id, String text, QuestionType type, boolean required,
                    String disqualifyingAnswer, List<String> subPoints) {
        this.id = Objects.requireNonNull(id, "Question ID cannot be null");
        this.text = Objects.requireNonNull(text, "Question text cannot be null");
        this.type = Objects.requireNonNull(type, "Question type cannot be null");
        this.required = required;
        this.disqualifyingAnswer = disqualifyingAnswer;
        this.subPoints = subPoints != null ? List.copyOf(subPoints) : List.of();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", type=" + type +
                ", required=" + required +
                '}';
    }
}
