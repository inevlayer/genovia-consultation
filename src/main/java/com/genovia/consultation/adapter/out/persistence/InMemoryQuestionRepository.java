package com.genovia.consultation.adapter.out.persistence;

import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.model.QuestionType;
import com.genovia.consultation.domain.port.out.QuestionRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class InMemoryQuestionRepository implements QuestionRepository {

    private final Map<String, List<Question>> questionsByProduct = new ConcurrentHashMap<>();

    public InMemoryQuestionRepository() {
        loadPearAllergyQuestions();
    }

    @Override
    public List<Question> findByProductId(String productId) {
        if (productId == null) {
            return List.of();
        }
        return questionsByProduct.getOrDefault(productId, List.of());
    }

    private void loadPearAllergyQuestions() {
        List<Question> pearAllergyQuestions = Arrays.asList(
                new Question(
                        "Q1",
                        "Are you aged 18 or over?",
                        QuestionType.YES_NO,
                        true,
                        "NO",
                        null
                ),
                new Question(
                        "Q2",
                        "Have you been diagnosed with a Genovian Pear allergy?",
                        QuestionType.YES_NO,
                        true,
                        "NO",
                        null
                ),
                new Question(
                        "Q3",
                        "Have you experienced anaphylaxis to Genovian Pears?",
                        QuestionType.YES_NO,
                        true,
                        "YES",
                        null
                ),
                new Question(
                        "Q4",
                        "Are you currently taking allergy medications?",
                        QuestionType.YES_NO,
                        false,
                        "YES",
                        null
                )
        );

        questionsByProduct.put("pear-allergy", pearAllergyQuestions);
    }

    public void addProductQuestions(String productId, List<Question> questions) {
        questionsByProduct.put(productId, questions);
    }
}
