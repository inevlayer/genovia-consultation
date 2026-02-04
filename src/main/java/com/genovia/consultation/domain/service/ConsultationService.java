package com.genovia.consultation.domain.service;

import com.genovia.consultation.adapter.out.messaging.ConsultationEventPublisher;
import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.Consultation;
import com.genovia.consultation.domain.model.EligibilityResult;
import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.port.in.GetConsultationUseCase;
import com.genovia.consultation.domain.port.in.GetQuestionsUseCase;
import com.genovia.consultation.domain.port.in.SubmitConsultationUseCase;
import com.genovia.consultation.domain.port.out.ConsultationRepository;
import com.genovia.consultation.domain.port.out.QuestionRepository;

import java.util.List;
import java.util.Optional;

public class ConsultationService implements GetQuestionsUseCase, SubmitConsultationUseCase, GetConsultationUseCase {

    private final QuestionRepository questionRepository;
    private final ConsultationRepository consultationRepository;
    private final EligibilityService eligibilityService;
    private final ConsultationEventPublisher eventPublisher;
    private final WorkflowService workflowService;

    public ConsultationService(QuestionRepository questionRepository,
                              ConsultationRepository consultationRepository,
                              EligibilityService eligibilityService,
                              ConsultationEventPublisher eventPublisher) {
        this.questionRepository = questionRepository;
        this.consultationRepository = consultationRepository;
        this.eligibilityService = eligibilityService;
        this.eventPublisher = eventPublisher;
        this.workflowService = new WorkflowService();
    }

    @Override
    public List<Question> getQuestions(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        List<Question> questions = questionRepository.findByProductId(productId);

        if (questions.isEmpty()) {
            throw new IllegalArgumentException("No questions found for product: " + productId);
        }

        return questions;
    }

    @Override
    public Consultation submitConsultation(String productId, List<Answer> answers) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }

        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Answers cannot be null or empty");
        }

        List<Question> questions = questionRepository.findByProductId(productId);

        if (questions.isEmpty()) {
            throw new IllegalArgumentException("No questions found for product: " + productId);
        }

        EligibilityResult eligibilityResult = eligibilityService.determineEligibility(productId, questions, answers);
        Consultation consultation = new Consultation(productId, answers, eligibilityResult);
        Consultation saved = consultationRepository.save(consultation);

        if (eligibilityResult.isEligible()) {
            workflowService.routeConsultation(saved, eventPublisher);
        }

        return saved;
    }

    @Override
    public Optional<Consultation> getConsultation(String consultationId) {
        if (consultationId == null || consultationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Consultation ID cannot be null or empty");
        }

        return consultationRepository.findById(consultationId);
    }
}
