package com.genovia.consultation.domain.model;


public record ConsultationSubmittedEvent(
        String consultationId,
        String productId,
        boolean preliminaryEligible
) {
    public static ConsultationSubmittedEvent from(Consultation consultation) {
        return new ConsultationSubmittedEvent(
                consultation.getId(),
                consultation.getProductId(),
                consultation.getEligibilityResult().isEligible()
        );
    }
}
