package com.genovia.consultation.domain.service.eligibility;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.EligibilityResult;
import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.model.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PearAllergyEligibilityStrategyTest {

    private PearAllergyEligibilityStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PearAllergyEligibilityStrategy();
    }

    @Test
    void shouldReturnCorrectProductId() {
        assertEquals("pear-allergy", strategy.getProductId());
    }

    @Test
    void shouldReturnEligible_whenAllAnswersAreValid() {
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null),
                new Question("Q2", "Do you have pear allergy?", QuestionType.YES_NO, true, "NO", null)
        );

        List<Answer> answers = List.of(
                new Answer("Q1", "YES"),
                new Answer("Q2", "YES")
        );

        EligibilityResult result = strategy.evaluate(questions, answers);

        assertTrue(result.isEligible());
        assertEquals("Your consultation has been submitted for review.", result.getReason());
    }

    @Test
    void shouldReturnIneligible_whenAnswerIsDisqualifying() {
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null),
                new Question("Q2", "Have you had anaphylaxis?", QuestionType.YES_NO, true, "YES", null)
        );

        List<Answer> answers = List.of(
                new Answer("Q1", "YES"),
                new Answer("Q2", "YES")  // Disqualifying answer
        );

        EligibilityResult result = strategy.evaluate(questions, answers);

        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("GP"));
    }

    @Test
    void shouldReturnIneligible_whenRequiredQuestionNotAnswered() {
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null),
                new Question("Q2", "Do you have pear allergy?", QuestionType.YES_NO, true, "NO", null)
        );

        List<Answer> answers = List.of(
                new Answer("Q1", "YES")
                // q2 is not answered
        );

        EligibilityResult result = strategy.evaluate(questions, answers);

        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("Q2"));
    }
}
