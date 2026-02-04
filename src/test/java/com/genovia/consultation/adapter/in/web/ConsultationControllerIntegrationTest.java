package com.genovia.consultation.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ConsultationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetQuestions_whenProductIdIsValid() throws Exception {
        mockMvc.perform(get("/api/consultations/questions")
                        .param("productId", "pear-allergy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("pear-allergy"))
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$.questions[0].id").exists())
                .andExpect(jsonPath("$.questions[0].text").exists())
                .andExpect(jsonPath("$.questions[0].type").exists())
                .andExpect(jsonPath("$.questions[0].required").isBoolean());
    }

    @Test
    void shouldSubmitConsultation_andReturnEligible_whenAllAnswersValid() throws Exception {
        String requestBody = """
                {
                    "productId": "pear-allergy",
                    "answers": [
                        {"questionId": "Q1", "answer": "YES"},
                        {"questionId": "Q2", "answer": "YES"},
                        {"questionId": "Q3", "answer": "NO"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consultationId").exists())
                .andExpect(jsonPath("$.productId").value("pear-allergy"))
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.message").value(containsString("submitted for review")))
                .andExpect(jsonPath("$.submittedAt").exists());
    }

    @Test
    void shouldSubmitConsultation_andReturnIneligible_whenAnswerIsDisqualifying() throws Exception {
        String requestBody = """
                {
                    "productId": "pear-allergy",
                    "answers": [
                        {"questionId": "Q1", "answer": "YES"},
                        {"questionId": "Q2", "answer": "YES"},
                        {"questionId": "Q3", "answer": "YES"},
                        {"questionId": "Q4", "answer": "NO"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consultationId").exists())
                .andExpect(jsonPath("$.eligible").value(false))
                .andExpect(jsonPath("$.message").value(containsString("GP")));
    }

    @Test
    void shouldReturnBadRequest_whenProductIdIsInvalid() throws Exception {
        String requestBody = """
                {
                    "productId": "non-existent-product",
                    "answers": [
                        {"questionId": "Q1", "answer": "YES"}
                    ]
                }
                """;

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldReturnBadRequest_whenAnswersAreEmpty() throws Exception {
        String requestBody = """
                {
                    "productId": "pear-allergy",
                    "answers": []
                }
                """;

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldGetConsultation_whenIdExists() throws Exception {
        String requestBody = """
                {
                    "productId": "pear-allergy",
                    "answers": [
                        {"questionId": "Q1", "answer": "YES"},
                        {"questionId": "Q2", "answer": "YES"},
                        {"questionId": "Q3", "answer": "NO"}
                    ]
                }
                """;

        MvcResult result = mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        String consultationId = JsonPath.read(((MvcResult) result).getResponse().getContentAsString(), "$.consultationId");

        mockMvc.perform(get("/api/consultations/" + consultationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultationId").value(consultationId))
                .andExpect(jsonPath("$.productId").value("pear-allergy"))
                .andExpect(jsonPath("$.eligible").value(true));
    }

    @Test
    void shouldReturnNotFound_whenConsultationIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/consultations/non-existent-id"))
                .andExpect(status().isNotFound());
    }
}
