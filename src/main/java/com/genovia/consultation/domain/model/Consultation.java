package com.genovia.consultation.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Consultation {
    private final String id;
    private final String productId;
    private final List<Answer> answers;
    private final Instant submittedAt;
    private final EligibilityResult eligibilityResult;
    private final ConsultationStatus status;
    private final DoctorReview doctorReview;

    public Consultation(String productId, List<Answer> answers, EligibilityResult eligibilityResult) {
        this(UUID.randomUUID().toString(), productId, answers, Instant.now(), eligibilityResult,
             ConsultationStatus.PENDING_REVIEW, null);
    }

    public Consultation(String id, String productId, List<Answer> answers,
                       Instant submittedAt, EligibilityResult eligibilityResult) {
        this(id, productId, answers, submittedAt, eligibilityResult, ConsultationStatus.PENDING_REVIEW, null);
    }

    public Consultation(String id, String productId, List<Answer> answers,
                       Instant submittedAt, EligibilityResult eligibilityResult,
                       ConsultationStatus status, DoctorReview doctorReview) {
        this.id = Objects.requireNonNull(id, "Consultation ID cannot be null");
        this.productId = Objects.requireNonNull(productId, "Product ID cannot be null");
        this.answers = Objects.requireNonNull(answers, "Answers cannot be null");
        this.submittedAt = Objects.requireNonNull(submittedAt, "Submitted time cannot be null");
        this.eligibilityResult = Objects.requireNonNull(eligibilityResult, "Eligibility result cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.doctorReview = doctorReview; // Can be null (not yet reviewed)
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public List<Answer> getAnswers() {
        return List.copyOf(answers);
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public EligibilityResult getEligibilityResult() {
        return eligibilityResult;
    }

    public ConsultationStatus getStatus() {
        return status;
    }

    public DoctorReview getDoctorReview() {
        return doctorReview;
    }

    
    public Consultation withDoctorReview(DoctorReview review) {
        ConsultationStatus newStatus = review.approved() ?
            ConsultationStatus.APPROVED : ConsultationStatus.REJECTED;
        return new Consultation(id, productId, answers, submittedAt,
                               eligibilityResult, newStatus, review);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consultation that = (Consultation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id='" + id + '\'' +
                ", productId='" + productId + '\'' +
                ", answersCount=" + answers.size() +
                ", submittedAt=" + submittedAt +
                ", eligible=" + eligibilityResult.isEligible() +
                '}';
    }
}
