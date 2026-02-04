package com.genovia.consultation.adapter.out.persistence;

import com.genovia.consultation.domain.model.Answer;
import com.genovia.consultation.domain.model.Consultation;
import com.genovia.consultation.domain.model.EligibilityResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryConsultationRepositoryTest {

    private InMemoryConsultationRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryConsultationRepository();
    }

    @Test
    void shouldSaveAndRetrieveConsultation() {

        Consultation consultation = new Consultation(
                "pear-allergy",
                List.of(new Answer("Q1", "YES")),
                EligibilityResult.eligible()
        );


        Consultation saved = repository.save(consultation);
        Optional<Consultation> retrieved = repository.findById(saved.getId());


        assertTrue(retrieved.isPresent());
        assertEquals(saved.getId(), retrieved.get().getId());
        assertEquals("pear-allergy", retrieved.get().getProductId());
    }

    @Test
    void shouldReturnEmptyOptional_whenConsultationNotFound() {

        Optional<Consultation> result = repository.findById("non-existent-id");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldOverwriteConsultation_whenSavingWithSameId() {
        Consultation original = new Consultation(
                "pear-allergy",
                List.of(new Answer("Q1", "YES")),
                EligibilityResult.eligible()
        );
        repository.save(original);


        Consultation updated = new Consultation(
                original.getId(),
                "hair-loss",
                List.of(new Answer("Q1", "NO")),
                original.getSubmittedAt(),
                EligibilityResult.ineligible("Different reason")
        );


        repository.save(updated);
        Optional<Consultation> result = repository.findById(original.getId());


        assertTrue(result.isPresent());
        assertEquals("hair-loss", result.get().getProductId());
    }

    @Test
    void shouldClearAllConsultations() {

        repository.save(new Consultation("pear-allergy", List.of(new Answer("Q1", "YES")), EligibilityResult.eligible()));
        repository.save(new Consultation("hair-loss", List.of(new Answer("Q1", "NO")), EligibilityResult.eligible()));
        assertEquals(2, repository.count());


        repository.clear();


        assertEquals(0, repository.count());
    }

    @Test
    void shouldCountConsultations() {

        assertEquals(0, repository.count());

        repository.save(new Consultation("pear-allergy", List.of(new Answer("Q1", "YES")), EligibilityResult.eligible()));
        repository.save(new Consultation("hair-loss", List.of(new Answer("Q1", "NO")), EligibilityResult.eligible()));

        assertEquals(2, repository.count());
    }

    @Test
    void shouldReturnEmpty_whenIdIsNull() {
        Optional<Consultation> result = repository.findById(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleEmptyId_gracefully() {
        Optional<Consultation> result = repository.findById("");

        assertTrue(result.isEmpty());
    }

}
