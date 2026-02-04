package com.genovia.consultation.domain.port.out;

import com.genovia.consultation.domain.model.Question;

import java.util.List;

public interface QuestionRepository {
    List<Question> findByProductId(String productId);
}
