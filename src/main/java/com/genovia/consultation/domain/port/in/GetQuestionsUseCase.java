package com.genovia.consultation.domain.port.in;

import com.genovia.consultation.domain.model.Question;

import java.util.List;

public interface GetQuestionsUseCase {
    List<Question> getQuestions(String productId);
}
