package com.genovia.consultation.domain.service.eligibility;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.EligibilityResult;
import com.genovia.consultation.domain.model.Question;

import java.util.List;

public interface EligibilityStrategy {
    EligibilityResult evaluate(List<Question> consultationQuestions, List<Answer> userProvidedAnswers);

    String getProductId();
}
