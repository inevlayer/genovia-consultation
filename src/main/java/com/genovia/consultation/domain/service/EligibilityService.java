package com.genovia.consultation.domain.service;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.EligibilityResult;
import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.service.eligibility.EligibilityStrategy;
import com.genovia.consultation.domain.service.eligibility.EligibilityStrategyFactory;

import java.util.List;
public class EligibilityService {
    private final EligibilityStrategyFactory strategyFactory;

    public EligibilityService(EligibilityStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public EligibilityResult determineEligibility(String productId,
                                                  List<Question>consultationQuestions,
                                                  List<Answer>providedAnswers) {
        EligibilityStrategy strategy = strategyFactory.getStrategy(productId);
        return strategy.evaluate(consultationQuestions, providedAnswers);
    }
}