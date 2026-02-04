package com.genovia.consultation.adapter.in.web;

import com.genovia.consultation.domain.model.Answer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.stream.Collectors;

public class SubmitConsultationRequest {
    @NotBlank(message = "Product ID blank")
    @Size(min = 3, max = 50, message = "Product ID 3-50 chars")
    private String productId;

    @NotEmpty(message = "At least one answer")
    @Valid
    private List<AnswerDto> answers;

    public SubmitConsultationRequest() {
    }

    public SubmitConsultationRequest(String productId, List<AnswerDto> answers) {
        this.productId = productId;
        this.answers = answers;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }

    public List<Answer> toAnswers() {
        return answers.stream()
                .map(dto -> new Answer(dto.getQuestionId(), dto.getAnswer()))
                .collect(Collectors.toList());
    }

    public static class AnswerDto {
        @NotBlank(message = "Question ID blank")
        @Size(max = 50, message = "Question ID max 50 chars")
        private String questionId;

        @NotBlank(message = "Answer blank")
        @Size(max = 1000, message = "Answer max 1000 chars")
        private String answer;

        public AnswerDto() {
        }

        public AnswerDto(String questionId, String answer) {
            this.questionId = questionId;
            this.answer = answer;
        }

        public String getQuestionId() {
            return questionId;
        }

        public void setQuestionId(String questionId) {
            this.questionId = questionId;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
