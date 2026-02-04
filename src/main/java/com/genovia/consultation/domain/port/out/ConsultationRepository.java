package com.genovia.consultation.domain.port.out;

import com.genovia.consultation.domain.model.Consultation;

import java.util.Optional;

public interface ConsultationRepository {
    Consultation save(Consultation consultation);

    Optional<Consultation> findById(String consultationId);
}
