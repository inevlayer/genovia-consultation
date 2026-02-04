package com.genovia.consultation.adapter.in.web;

import com.genovia.consultation.domain.model.*;
import com.genovia.consultation.domain.port.in.GetConsultationUseCase;
import com.genovia.consultation.domain.port.in.GetQuestionsUseCase;
import com.genovia.consultation.domain.port.in.SubmitConsultationUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ConsultationController.class)
class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetQuestionsUseCase getQuestionsUseCase;

    @MockBean
    private SubmitConsultationUseCase submitConsultationUseCase;

    @MockBean
    private GetConsultationUseCase getConsultationUseCase;

    @Test
    void shouldReturnQuestions_whenValidProductId() throws Exception {
        String productId = "pear-allergy";
        List<Question> questions = List.of(
                new Question("Q1", "Are you 18+?", QuestionType.YES_NO, true, "NO", null),
                new Question("Q2", "Do you have allergies?", QuestionType.YES_NO, true, "NO", null)
        );

        when(getQuestionsUseCase.getQuestions(productId)).thenReturn(questions);


        mockMvc.perform(get("/api/consultations/questions")
                        .param("productId", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.questions").isArray())
                .andExpect(jsonPath("$.questions", hasSize(2)))
                .andExpect(jsonPath("$.questions[0].id").value("Q1"))
                .andExpect(jsonPath("$.questions[0].text").value("Are you 18+?"))
                .andExpect(jsonPath("$.questions[0].type").value("YES_NO"))
                .andExpect(jsonPath("$.questions[0].required").value(true));

        verify(getQuestionsUseCase).getQuestions(productId);
    }

    @Test
    void shouldReturnBadRequest_whenProductIdIsInvalid() throws Exception {

        when(getQuestionsUseCase.getQuestions("invalid-product"))
                .thenThrow(new IllegalArgumentException("No questions found for product: invalid-product"));


        mockMvc.perform(get("/api/consultations/questions")
                        .param("productId", "invalid-product"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No questions found for product: invalid-product"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }


    @Test
    void shouldSubmitConsultation_andReturnCreated_whenRequestIsValid() throws Exception {

        String productId = "pear-allergy";
        List<Answer> answers = List.of(
                new Answer("Q1", "YES"),
                new Answer("Q2", "NO")
        );

        Consultation consultation = new Consultation(
                productId,
                answers,
                EligibilityResult.eligible()
        );

        when(submitConsultationUseCase.submitConsultation(eq(productId), any()))
                .thenReturn(consultation);

        String requestBody = """
                {
                    "productId": "pear-allergy",
                    "answers": [
                        {"questionId": "Q1", "answer": "YES"},
                        {"questionId": "Q2", "answer": "NO"}
                    ]
                }
                """;


        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.consultationId").exists())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.eligible").value(true))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.submittedAt").exists());

        verify(submitConsultationUseCase).submitConsultation(eq(productId), any());
    }



    @Test
    void shouldReturnIneligibleConsultation_whenAnswersDisqualify() throws Exception {

        String productId = "pear-allergy";
        List<Answer> answers = List.of(
                new Answer("Q1", "YES"),
                new Answer("Q2", "YES")  // Disqualifying
        );

        Consultation consultation = new Consultation(
                productId,
                answers,
                EligibilityResult.ineligible("Based on your answers, we recommend speaking with your GP.")
        );

        when(submitConsultationUseCase.submitConsultation(eq(productId), any()))
                .thenReturn(consultation);

        String requestBody = """
                {
                    "productId": "pear-allergy",
                    "answers": [
                        {"questionId": "Q1", "answer": "YES"},
                        {"questionId": "Q2", "answer": "YES"}
                    ]
                }
                """;


        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eligible").value(false))
                .andExpect(jsonPath("$.message").value(containsString("GP")));
    }

    @Test
    void shouldReturnConsultation_whenIdExists() throws Exception {

        String consultationId = "test-consultation-id";
        Consultation consultation = new Consultation(
                "pear-allergy",
                List.of(new Answer("Q1", "YES")),
                EligibilityResult.eligible()
        );

        when(getConsultationUseCase.getConsultation(consultationId))
                .thenReturn(Optional.of(consultation));

        mockMvc.perform(get("/api/consultations/{id}", consultationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consultationId").exists())
                .andExpect(jsonPath("$.productId").value("pear-allergy"))
                .andExpect(jsonPath("$.eligible").value(true));

        verify(getConsultationUseCase).getConsultation(consultationId);
    }

    @Test
    void shouldReturnNotFound_whenConsultationIdDoesNotExist() throws Exception {

        String consultationId = "non-existent-id";
        when(getConsultationUseCase.getConsultation(consultationId))
                .thenReturn(Optional.empty());


        mockMvc.perform(get("/api/consultations/{id}", consultationId))
                .andExpect(status().isNotFound());

        verify(getConsultationUseCase).getConsultation(consultationId);
    }


    @Test
    void shouldUseDefaultProductId_whenNotProvided() throws Exception {

        List<Question> questions = List.of(
                new Question("Q1", "Test question", QuestionType.YES_NO, true, "NO", null)
        );

        when(getQuestionsUseCase.getQuestions("pear-allergy")).thenReturn(questions);


        mockMvc.perform(get("/api/consultations/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("pear-allergy"));

        verify(getQuestionsUseCase).getQuestions("pear-allergy");
    }

    @Test
    void shouldHandleUnexpectedException_andReturn500() throws Exception {

        when(getQuestionsUseCase.getQuestions(any()))
                .thenThrow(new RuntimeException("Unexpected database error"));


        mockMvc.perform(get("/api/consultations/questions")
                        .param("productId", "pear-allergy"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.status").value(500));
    }
}
