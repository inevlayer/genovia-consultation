package com.genovia.consultation.domain.port.in;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.Consultation;

import java.util.List;

public interface SubmitConsultationUseCase {
    Consultation submitConsultation(String productId, List<Answer> answersToConsultationQs);
}
