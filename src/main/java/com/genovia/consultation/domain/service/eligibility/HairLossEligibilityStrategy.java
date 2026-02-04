package com.genovia.consultation.domain.service.eligibility;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.EligibilityResult;
import com.genovia.consultation.domain.model.Question;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HairLossEligibilityStrategy implements EligibilityStrategy {

    private static final String INELIGIBLE_MESSAGE = "Based on your medical history, we recommend consulting with a specialist before starting treatment.";

    @Override
    public EligibilityResult evaluate(List<Question> consultationQuestions, List<Answer> providedAnswers) {
        Map<String, String> answerMap = providedAnswers
                .stream()
                .collect(Collectors.toMap(Answer::questionId, Answer::answer));

        for (Question question : consultationQuestions) {
            String answer = answerMap.get(question.id());

            if (question.required() && (answer == null || answer.trim().isEmpty())) {
                return EligibilityResult.ineligible(
                        String.format("Required question '%s' was not answered.", question.id())
                );
            }

            if (question.disqualifyingAnswer() != null &&
                question.disqualifyingAnswer().equalsIgnoreCase(answer)) {
                return EligibilityResult.ineligible(INELIGIBLE_MESSAGE);
            }
        }

        return EligibilityResult.eligible();
    }

    @Override
    public String getProductId() {
        return "hair-loss";
    }
}
