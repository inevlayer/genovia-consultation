package com.genovia.consultation.domain.service;

import com.genovia.consultation.adapter.out.messaging.ConsultationEventPublisher;
import com.genovia.consultation.domain.model.Consultation;
import com.genovia.consultation.domain.model.ConsultationSubmittedEvent;
import com.genovia.consultation.domain.model.ReviewWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowService {
    private static final Logger log = LoggerFactory.getLogger(WorkflowService.class);

    public ReviewWorkflow getWorkflowForProduct(String productId) {
        if ("pear-allergy".equals(productId)) {
            return ReviewWorkflow.AUTOMATED;
        }
        return ReviewWorkflow.ASYNC_DOCTOR_REVIEW;
    }

    public void routeConsultation(Consultation consultation, ConsultationEventPublisher eventPublisher) {
        ReviewWorkflow workflow = getWorkflowForProduct(consultation.getProductId());

        switch (workflow) {
            case AUTOMATED:
                log.info("Automated workflow for {}", consultation.getId());
                break;

            case ASYNC_DOCTOR_REVIEW:
                if (eventPublisher != null) {
                    log.info("Publishing {} for async review", consultation.getId());
                    eventPublisher.publishConsultationSubmitted(
                            ConsultationSubmittedEvent.from(consultation)
                    );
                } else {
                    log.warn("Kafka disabled for {}", consultation.getId());
                }
                break;

            case SYNC_DOCTOR_REVIEW:
                log.info("Sync review not implemented for {}", consultation.getId());
                break;
        }
    }
}
