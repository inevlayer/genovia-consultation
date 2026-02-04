package com.genovia.consultation.domain.service;

import com.genovia.consultation.adapter.out.messaging.ConsultationEventPublisher;
import com.genovia.consultation.domain.model.*;
import com.genovia.consultation.domain.port.out.ConsultationRepository;
import com.genovia.consultation.domain.port.out.QuestionRepository;
import com.genovia.consultation.domain.service.eligibility.EligibilityStrategyFactory;
import com.genovia.consultation.domain.service.eligibility.HairLossEligibilityStrategy;
import com.genovia.consultation.domain.service.eligibility.PearAllergyEligibilityStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConsultationServiceTest {

    private ConsultationService consultationService;
    private QuestionRepository questionRepository;
    private ConsultationRepository consultationRepository;
    private EligibilityService eligibilityService;
    private ConsultationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        questionRepository = mock(QuestionRepository.class);
        consultationRepository = mock(ConsultationRepository.class);

        EligibilityStrategyFactory strategyFactory = new EligibilityStrategyFactory(
                List.of(new PearAllergyEligibilityStrategy(), new HairLossEligibilityStrategy())
        );
        eligibilityService = new EligibilityService(strategyFactory);

        consultationService = new ConsultationService(
                questionRepository,
                consultationRepository,
                eligibilityService,
                null
        );
    }

    @Test
    void shouldGetQuestions_whenProductIdIsValid() {

        String productId = "pear-allergy";
        List<Question> expectedQuestions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null)
        );
        when(questionRepository.findByProductId(productId)).thenReturn(expectedQuestions);


        List<Question> result = consultationService.getQuestions(productId);


        assertEquals(expectedQuestions, result);
        verify(questionRepository).findByProductId(productId);
    }

    @Test
    void shouldThrowException_whenProductIdIsNull() {

        assertThrows(IllegalArgumentException.class, () ->
                consultationService.getQuestions(null)
        );
    }

    @Test
    void shouldThrowException_whenNoQuestionsFoundForProduct() {

        when(questionRepository.findByProductId(any())).thenReturn(List.of());


        assertThrows(IllegalArgumentException.class, () ->
                consultationService.getQuestions("unknown-product")
        );
    }

    @Test
    void shouldSubmitConsultation_andReturnEligible_whenAnswersAreValid() {

        String productId = "pear-allergy";
        List<Question> questions = List.of(new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null));
        List<Answer> answers = List.of(new Answer("Q1", "YES"));

        when(questionRepository.findByProductId(productId)).thenReturn(questions);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Consultation result = consultationService.submitConsultation(productId, answers);

        assertNotNull(result);
        assertTrue(result.getEligibilityResult().isEligible());
        assertEquals(productId, result.getProductId());
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void shouldSubmitConsultation_andReturnIneligible_whenAnswersAreDisqualifying() {

        String productId = "pear-allergy";
        List<Question> questions = List.of(
                new Question("Q1", "Have you had anaphylaxis?", QuestionType.YES_NO, true, "YES", null)
        );
        List<Answer> answers = List.of(
                new Answer("Q1", "YES")
        );

        when(questionRepository.findByProductId(productId)).thenReturn(questions);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(invocation ->
                invocation.getArgument(0)
        );


        Consultation result = consultationService.submitConsultation(productId, answers);


        assertNotNull(result);
        assertFalse(result.getEligibilityResult().isEligible());
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void shouldThrowException_whenSubmittingWithNullProductId() {
        assertThrows(IllegalArgumentException.class, () ->
                consultationService.submitConsultation(null, List.of())
        );
    }

    @Test
    void shouldThrowException_whenSubmittingWithEmptyAnswers() {
        assertThrows(IllegalArgumentException.class, () ->
                consultationService.submitConsultation("pear-allergy", List.of())
        );
    }

    @Test
    void shouldGetConsultation_whenIdExists() {
        String consultationId = "test-id";
        Consultation expectedConsultation = new Consultation(
                "pear-allergy",
                List.of(new Answer("Q1", "YES")),
                EligibilityResult.eligible()
        );
        when(consultationRepository.findById(consultationId)).thenReturn(Optional.of(expectedConsultation));

        Optional<Consultation> result = consultationService.getConsultation(consultationId);

        assertTrue(result.isPresent());
        verify(consultationRepository).findById(consultationId);
    }

    @Test
    void shouldReturnEmpty_whenConsultationIdDoesNotExist() {
        when(consultationRepository.findById(any())).thenReturn(Optional.empty());

        Optional<Consultation> result = consultationService.getConsultation("non-existent-id");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldThrowException_whenGettingConsultationWithNullId() {
        assertThrows(IllegalArgumentException.class, () ->
                consultationService.getConsultation(null)
        );
    }

    @Test
    void shouldPublishEvent_whenConsultationIsEligibleAndKafkaEnabled() {
        eventPublisher = mock(ConsultationEventPublisher.class);
        ConsultationService serviceWithKafka = new ConsultationService(
                questionRepository,
                consultationRepository,
                eligibilityService,
                eventPublisher
        );

        String productId = "hair-loss";
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null)
        );
        List<Answer> answers = List.of(new Answer("Q1", "YES"));

        when(questionRepository.findByProductId(productId)).thenReturn(questions);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(i -> i.getArgument(0));

        Consultation result = serviceWithKafka.submitConsultation(productId, answers);

        verify(eventPublisher, times(1)).publishConsultationSubmitted(any(ConsultationSubmittedEvent.class));
    }

    @Test
    void shouldNotPublishEvent_whenProductIsAutomated() {
        eventPublisher = mock(ConsultationEventPublisher.class);
        ConsultationService serviceWithKafka = new ConsultationService(
                questionRepository,
                consultationRepository,
                eligibilityService,
                eventPublisher
        );

        String productId = "pear-allergy";
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null)
        );
        List<Answer> answers = List.of(new Answer("Q1", "YES"));

        when(questionRepository.findByProductId(productId)).thenReturn(questions);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(i -> i.getArgument(0));

        Consultation result = serviceWithKafka.submitConsultation(productId, answers);

        verify(eventPublisher, never()).publishConsultationSubmitted(any(ConsultationSubmittedEvent.class));
    }

    @Test
    void shouldNotPublishEvent_whenKafkaDisabled() {
        String productId = "hair-loss";
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null)
        );
        List<Answer> answers = List.of(new Answer("Q1", "YES"));

        when(questionRepository.findByProductId(productId)).thenReturn(questions);
        when(consultationRepository.save(any(Consultation.class))).thenAnswer(i -> i.getArgument(0));

        Consultation result = consultationService.submitConsultation(productId, answers);

        assertNotNull(result);
    }
}
