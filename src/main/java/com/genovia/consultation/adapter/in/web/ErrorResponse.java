package com.genovia.consultation.adapter.in.web;

import java.time.Instant;

public record ErrorResponse(String message, String code, int status, String path, Instant timestamp) {
    public ErrorResponse(String error) {
        this(error, "ERROR", 400, "", Instant.now());
    }
    public String getError() {
        return message;
    }
}
