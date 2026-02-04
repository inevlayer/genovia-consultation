package com.genovia.consultation.adapter.in.web;

import com.genovia.consultation.domain.model.Consultation;

public class ConsultationResponse {
    private String consultationId;
    private String productId;
    private boolean eligible;
    private String message;
    private String submittedAt;
    private String status;
    private DoctorReviewResponse doctorReview;

    public ConsultationResponse() {
    }

    public ConsultationResponse(String consultationId, String productId, boolean eligible,
                               String message, String submittedAt, String status,
                               DoctorReviewResponse doctorReview) {
        this.consultationId = consultationId;
        this.productId = productId;
        this.eligible = eligible;
        this.message = message;
        this.submittedAt = submittedAt;
        this.status = status;
        this.doctorReview = doctorReview;
    }

    public static ConsultationResponse from(Consultation consultation) {
        DoctorReviewResponse reviewResponse = consultation.getDoctorReview() != null ?
            new DoctorReviewResponse(
                consultation.getDoctorReview().reviewedBy(),
                consultation.getDoctorReview().reviewedAt().toString(),
                consultation.getDoctorReview().approved(),
                consultation.getDoctorReview().notes()
            ) : null;

        return new ConsultationResponse(
                consultation.getId(),
                consultation.getProductId(),
                consultation.getEligibilityResult().isEligible(),
                consultation.getEligibilityResult().getReason(),
                consultation.getSubmittedAt().toString(),
                consultation.getStatus().name(),
                reviewResponse
        );
    }

    public String getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(String consultationId) {
        this.consultationId = consultationId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DoctorReviewResponse getDoctorReview() {
        return doctorReview;
    }

    public void setDoctorReview(DoctorReviewResponse doctorReview) {
        this.doctorReview = doctorReview;
    }

    public static class DoctorReviewResponse {
        private String reviewedBy;
        private String reviewedAt;
        private boolean approved;
        private String notes;

        public DoctorReviewResponse() {
        }

        public DoctorReviewResponse(String reviewedBy, String reviewedAt, boolean approved, String notes) {
            this.reviewedBy = reviewedBy;
            this.reviewedAt = reviewedAt;
            this.approved = approved;
            this.notes = notes;
        }

        public String getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
        public String getReviewedAt() { return reviewedAt; }
        public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }
        public boolean isApproved() { return approved; }
        public void setApproved(boolean approved) { this.approved = approved; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
