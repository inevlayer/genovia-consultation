package com.genovia.consultation.adapter.out.messaging;

import com.genovia.consultation.domain.model.ConsultationSubmittedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true", matchIfMissing = false)
public class ConsultationEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(ConsultationEventPublisher.class);
    private static final String TOPIC = "consultation-submitted";

    private final KafkaTemplate<String, ConsultationSubmittedEvent> kafkaTemplate;

    public ConsultationEventPublisher(KafkaTemplate<String, ConsultationSubmittedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishConsultationSubmitted(ConsultationSubmittedEvent event) {
        log.info("Publishing event: {}", event.consultationId());
        kafkaTemplate.send(TOPIC, event.consultationId(), event);
    }
}
