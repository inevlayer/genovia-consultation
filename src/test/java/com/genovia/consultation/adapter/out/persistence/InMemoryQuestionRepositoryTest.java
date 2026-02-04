package com.genovia.consultation.adapter.out.persistence;

import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.model.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryQuestionRepositoryTest {

    private InMemoryQuestionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryQuestionRepository();
    }

    @Test
    void shouldFindPreloadedPearAllergyQuestions() {

        List<Question> questions = repository.findByProductId("pear-allergy");


        assertNotNull(questions);
        assertEquals(4, questions.size());
        assertEquals("Q1", questions.get(0).id());
        assertEquals("Are you aged 18 or over?", questions.get(0).text());
    }

    @Test
    void shouldReturnEmptyList_whenProductIdNotFound() {

        List<Question> questions = repository.findByProductId("non-existent-product");


        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    @Test
    void shouldAddNewProductQuestions() {

        List<Question> hairLossQuestions = List.of(
                new Question("HL1", "Are you male?", QuestionType.YES_NO, true, "NO", null),
                new Question("HL2", "Are you aged 18-65?", QuestionType.YES_NO, true, "NO", null)
        );


        repository.addProductQuestions("hair-loss", hairLossQuestions);
        List<Question> result = repository.findByProductId("hair-loss");


        assertEquals(2, result.size());
        assertEquals("HL1", result.get(0).id());
        assertEquals("Are you male?", result.get(0).text());
    }

    @Test
    void shouldOverwriteExistingProduct_whenAddingAgain() {

        List<Question> original = List.of(
                new Question("Q1", "Original question", QuestionType.YES_NO, true, "NO", null)
        );
        List<Question> updated = List.of(
                new Question("Q1", "Updated question", QuestionType.YES_NO, true, "NO", null)
        );


        repository.addProductQuestions("test-product", original);
        repository.addProductQuestions("test-product", updated);
        List<Question> result = repository.findByProductId("test-product");


        assertEquals(1, result.size());
        assertEquals("Updated question", result.get(0).text());
    }


    @Test
    void shouldReturnEmpty_whenProductIdIsNull() {
        List<Question> questions = repository.findByProductId(null);

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    @Test
    void shouldHandleEmptyProductId_gracefully() {
        List<Question> questions = repository.findByProductId("");

        assertNotNull(questions);
        assertTrue(questions.isEmpty());
    }

    @Test
    void shouldAllowAddingEmptyQuestionList() {
        List<Question> emptyList = List.of();

        repository.addProductQuestions("empty-product", emptyList);
        List<Question> result = repository.findByProductId("empty-product");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
