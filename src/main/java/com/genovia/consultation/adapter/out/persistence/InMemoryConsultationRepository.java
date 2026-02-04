package com.genovia.consultation.adapter.out.persistence;

import com.genovia.consultation.domain.model.Consultation;
import com.genovia.consultation.domain.port.out.ConsultationRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class InMemoryConsultationRepository implements ConsultationRepository {
    private final Map<String, Consultation> consultations = new ConcurrentHashMap<>();

    @Override
    public Consultation save(Consultation consultation) {
        consultations.put(consultation.getId(), consultation);
        return consultation;
    }

    @Override
    public Optional<Consultation> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(consultations.get(id));
    }

    public void clear() {
        consultations.clear();
    }

    public int count() {
        return consultations.size();
    }
}
