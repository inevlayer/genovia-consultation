package com.genovia.consultation.config;

import com.genovia.consultation.adapter.out.persistence.InMemoryConsultationRepository;
import com.genovia.consultation.adapter.out.persistence.InMemoryQuestionRepository;
import com.genovia.consultation.domain.port.in.GetConsultationUseCase;
import com.genovia.consultation.domain.port.in.GetQuestionsUseCase;
import com.genovia.consultation.domain.port.in.SubmitConsultationUseCase;
import com.genovia.consultation.domain.port.out.ConsultationRepository;
import com.genovia.consultation.domain.port.out.QuestionRepository;
import com.genovia.consultation.domain.service.ConsultationService;
import com.genovia.consultation.domain.service.EligibilityService;
import com.genovia.consultation.domain.service.eligibility.EligibilityStrategy;
import com.genovia.consultation.domain.service.eligibility.EligibilityStrategyFactory;
import com.genovia.consultation.domain.service.eligibility.HairLossEligibilityStrategy;
import com.genovia.consultation.domain.service.eligibility.PearAllergyEligibilityStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class BeanConfiguration {

    @Bean
    public QuestionRepository questionRepository() {
        return new InMemoryQuestionRepository();
    }

    @Bean
    public ConsultationRepository consultationRepository() {
        return new InMemoryConsultationRepository();
    }

    @Bean
    public EligibilityStrategy pearAllergyStrategy() {
        return new PearAllergyEligibilityStrategy();
    }

    @Bean
    public EligibilityStrategy hairLossStrategy() {
        return new HairLossEligibilityStrategy();
    }

    @Bean
    public EligibilityStrategyFactory eligibilityStrategyFactory(List<EligibilityStrategy> strategies) {
        return new EligibilityStrategyFactory(strategies);
    }

    @Bean
    public EligibilityService eligibilityService(EligibilityStrategyFactory strategyFactory) {
        return new EligibilityService(strategyFactory);
    }

    @Bean
    public ConsultationService consultationService(
            QuestionRepository questionRepository,
            ConsultationRepository consultationRepository,
            EligibilityService eligibilityService,
            @org.springframework.beans.factory.annotation.Autowired(required = false)
            com.genovia.consultation.adapter.out.messaging.ConsultationEventPublisher eventPublisher) {
        return new ConsultationService(questionRepository, consultationRepository, eligibilityService, eventPublisher);
    }

    @Bean
    public GetQuestionsUseCase getQuestionsUseCase(ConsultationService consultationService) {
        return consultationService;
    }

    @Bean
    public SubmitConsultationUseCase submitConsultationUseCase(ConsultationService consultationService) {
        return consultationService;
    }

    @Bean
    public GetConsultationUseCase getConsultationUseCase(ConsultationService consultationService) {
        return consultationService;
    }
}
