package com.genovia.consultation.domain.model;

import java.time.Instant;


public record DoctorReview(
        String reviewedBy,
        Instant reviewedAt,
        boolean approved,
        String notes
) {
    public static DoctorReview approved(String reviewedBy, String notes) {
        return new DoctorReview(reviewedBy, Instant.now(), true, notes);
    }
    
    public static DoctorReview rejected(String reviewedBy, String reason) {
        return new DoctorReview(reviewedBy, Instant.now(), false, reason);
    }
}
