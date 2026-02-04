package com.genovia.consultation.domain.service.eligibility;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.EligibilityResult;
import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.model.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HairLossEligibilityStrategyTest {

    private HairLossEligibilityStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new HairLossEligibilityStrategy();
    }

    @Test
    void shouldReturnCorrectProductId() {
        assertEquals("hair-loss", strategy.getProductId());
    }

    @Test
    void shouldReturnEligible_whenAllAnswersAreValid() {

        List<Question> questions = List.of(
                new Question("HL1", "Are you male and aged 18-65?", QuestionType.YES_NO, true, "NO", null),
                new Question("HL2", "Do you have male pattern baldness?", QuestionType.YES_NO, true, "NO", null)
        );

        List<Answer> answers = List.of(
                new Answer("HL1", "YES"),
                new Answer("HL2", "YES")
        );


        EligibilityResult result = strategy.evaluate(questions, answers);

        assertTrue(result.isEligible());
        assertEquals("Your consultation has been submitted for review.", result.getReason());
    }

    @Test
    void shouldReturnIneligible_whenAnswerIsDisqualifying() {

        List<Question> questions = List.of(
                new Question("HL1", "Are you male and aged 18-65?", QuestionType.YES_NO, true, "NO", null),
                new Question("HL2", "Do you have heart conditions?", QuestionType.YES_NO, true, "YES", null)
        );

        List<Answer> answers = List.of(
                new Answer("HL1", "YES"),
                new Answer("HL2", "YES")  // Disqualifying answer
        );


        EligibilityResult result = strategy.evaluate(questions, answers);


        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("specialist"));
        assertTrue(result.getReason().contains("medical history"));
    }

    @Test
    void shouldReturnIneligible_whenRequiredQuestionNotAnswered() {

        List<Question> questions = List.of(
                new Question("HL1", "Are you male and aged 18-65?", QuestionType.YES_NO, true, "NO", null),
                new Question("HL2", "Do you have male pattern baldness?", QuestionType.YES_NO, true, "NO", null)
        );

        List<Answer> answers = List.of(
                new Answer("HL1", "YES")
        );


        EligibilityResult result = strategy.evaluate(questions, answers);

        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("HL2"));
        assertTrue(result.getReason().contains("not answered"));
    }

    @Test
    void shouldReturnIneligible_whenAnswerIsEmpty() {
        List<Question> questions = List.of(
                new Question("HL1", "Are you male and aged 18-65?", QuestionType.YES_NO, true, "NO", null)
        );

        List<Answer> answers = List.of(
                new Answer("HL1", "   ")
        );


        EligibilityResult result = strategy.evaluate(questions, answers);


        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("HL1"));
    }

    @Test
    void shouldBeCaseInsensitive_whenCheckingDisqualifyingAnswers() {
        List<Question> questions = List.of(
                new Question("HL1", "Do you have heart conditions?", QuestionType.YES_NO, true, "YES", null)
        );

        List<Answer> answers = List.of(
                new Answer("HL1", "yes")  // Lowercase but should match "YES"
        );


        EligibilityResult result = strategy.evaluate(questions, answers);


        assertFalse(result.isEligible());
        assertTrue(result.getReason().contains("specialist"));
    }

    @Test
    void shouldHandleMultipleQuestions_andStopAtFirstDisqualifying() {

        List<Question> questions = List.of(
                new Question("HL1", "Are you male and aged 18-65?", QuestionType.YES_NO, true, "NO", null),
                new Question("HL2", "Do you have heart conditions?", QuestionType.YES_NO, true, "YES", null),
                new Question("HL3", "Are you taking blood thinners?", QuestionType.YES_NO, true, "YES", null)
        );

        List<Answer> answers = List.of(
                new Answer("HL1", "YES"),
                new Answer("HL2", "YES"),
                new Answer("HL3", "YES")
        );


        EligibilityResult result = strategy.evaluate(questions, answers);


        assertFalse(result.isEligible());

    }
}
