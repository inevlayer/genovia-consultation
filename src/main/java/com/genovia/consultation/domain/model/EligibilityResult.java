package com.genovia.consultation.domain.model;

import java.util.Objects;

public class EligibilityResult {
    private final boolean eligible;
    private final String reason;

    private EligibilityResult(boolean eligible, String reason) {
        this.eligible = eligible;
        this.reason = reason;
    }

    public static EligibilityResult eligible() {
        return new EligibilityResult(true, "Your consultation has been submitted for review.");
    }

    public static EligibilityResult ineligible(String reason) {
        Objects.requireNonNull(reason, "Ineligibility reason cannot be null");
        return new EligibilityResult(false, reason);
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EligibilityResult that = (EligibilityResult) o;
        return eligible == that.eligible && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eligible, reason);
    }

    @Override
    public String toString() {
        return "EligibilityResult{" +
                "eligible=" + eligible +
                ", reason='" + reason + '\'' +
                '}';
    }
}
