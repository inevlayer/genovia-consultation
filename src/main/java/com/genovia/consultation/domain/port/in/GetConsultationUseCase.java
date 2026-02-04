package com.genovia.consultation.domain.port.in;

import com.genovia.consultation.domain.model.Consultation;

import java.util.Optional;

public interface GetConsultationUseCase {
    Optional<Consultation> getConsultation(String consultationId);
}
