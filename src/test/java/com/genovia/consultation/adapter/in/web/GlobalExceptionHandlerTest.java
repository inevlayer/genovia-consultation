package com.genovia.consultation.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    void shouldHandleIllegalArgumentException_andReturn400() {
        IllegalArgumentException exception = new IllegalArgumentException("Product ID cannot be null");
        when(mockRequest.getRequestURI()).thenReturn("/api/consultations/questions");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Product ID cannot be null", body.message());
        assertEquals("VALIDATION_ERROR", body.code());
        assertEquals(400, body.status());
        assertEquals("/api/consultations/questions", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void shouldHandleIllegalArgumentException_withDifferentMessage() {
        IllegalArgumentException exception = new IllegalArgumentException("Answers cannot be empty");
        when(mockRequest.getRequestURI()).thenReturn("/api/consultations");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(exception, mockRequest);

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Answers cannot be empty", body.message());
        assertEquals("VALIDATION_ERROR", body.code());
        assertEquals("/api/consultations", body.path());
    }

    @Test
    void shouldHandleUnexpectedException_andReturn500() {
        RuntimeException exception = new RuntimeException("Database connection failed");
        when(mockRequest.getRequestURI()).thenReturn("/api/consultations/questions");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpectedError(exception, mockRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Unexpected error", body.message());
        assertEquals("INTERNAL_ERROR", body.code());
        assertEquals(500, body.status());
        assertEquals("/api/consultations/questions", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void shouldNotExposeInternalDetails_in500Error() {
        RuntimeException exception = new RuntimeException("SQL injection detected in query: SELECT * FROM users WHERE password='admin123'");
        when(mockRequest.getRequestURI()).thenReturn("/api/consultations");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpectedError(exception, mockRequest);


        ErrorResponse body = response.getBody();
        assertNotNull(body);

        assertFalse(body.message().contains("SQL"));
        assertFalse(body.message().contains("password"));
        assertFalse(body.message().contains("admin123"));

        assertEquals("Unexpected error", body.message());
    }
}
