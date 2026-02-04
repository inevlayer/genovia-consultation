package com.genovia.consultation.adapter.out.messaging;

import com.genovia.consultation.domain.model.ConsultationSubmittedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ConsultationEventPublisherTest {

    private ConsultationEventPublisher publisher;
    private KafkaTemplate<String, ConsultationSubmittedEvent> kafkaTemplate;

    @BeforeEach
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        publisher = new ConsultationEventPublisher(kafkaTemplate);
    }

    @Test
    void shouldPublishEventToCorrectTopic() {
        ConsultationSubmittedEvent event = new ConsultationSubmittedEvent(
                "consultation-123",
                "pear-allergy",
                true
        );

        publisher.publishConsultationSubmitted(event);

        verify(kafkaTemplate, times(1)).send(
                eq("consultation-submitted"),
                eq("consultation-123"),
                eq(event)
        );
    }

    @Test
    void shouldUseConsultationIdAsKafkaKey() {
        String consultationId = "test-consultation-456";
        ConsultationSubmittedEvent event = new ConsultationSubmittedEvent(
                consultationId,
                "hair-loss",
                false
        );

        publisher.publishConsultationSubmitted(event);

        verify(kafkaTemplate).send(
                eq("consultation-submitted"),
                eq(consultationId),
                any(ConsultationSubmittedEvent.class)
        );
    }
}
