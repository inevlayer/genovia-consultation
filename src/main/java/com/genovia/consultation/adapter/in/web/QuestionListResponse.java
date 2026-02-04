package com.genovia.consultation.adapter.in.web;

import com.genovia.consultation.domain.model.Question;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionListResponse {
    private String productId;
    private List<QuestionDto> questions;

    public QuestionListResponse() {
    }

    public QuestionListResponse(String productId, List<QuestionDto> questions) {
        this.productId = productId;
        this.questions = questions;
    }

    public static QuestionListResponse from(String productId, List<Question> questions) {
        List<QuestionDto> questionDtos = questions.stream()
                .map(QuestionDto::from)
                .collect(Collectors.toList());
        return new QuestionListResponse(productId, questionDtos);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public static class QuestionDto {
        private String id;
        private String text;
        private String type;
        private boolean required;
        private List<String> subPoints;

        public QuestionDto() {
        }

        public static QuestionDto from(Question question) {
            QuestionDto dto = new QuestionDto();
            dto.id = question.id();
            dto.text = question.text();
            dto.type = question.type().name();
            dto.required = question.required();
            dto.subPoints = question.subPoints().isEmpty() ? null : question.subPoints();
            return dto;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public List<String> getSubPoints() {
            return subPoints;
        }

        public void setSubPoints(List<String> subPoints) {
            this.subPoints = subPoints;
        }
    }
}
