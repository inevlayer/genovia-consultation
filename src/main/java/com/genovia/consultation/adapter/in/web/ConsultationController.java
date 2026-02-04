package com.genovia.consultation.adapter.in.web;

import com.genovia.consultation.domain.model.Consultation;
import com.genovia.consultation.domain.model.Question;
import com.genovia.consultation.domain.port.in.GetConsultationUseCase;
import com.genovia.consultation.domain.port.in.GetQuestionsUseCase;
import com.genovia.consultation.domain.port.in.SubmitConsultationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@Tag(name = "Consultations", description = "Online consultation APIs")
public class ConsultationController {

    private final GetQuestionsUseCase getQuestionsUseCase;
    private final SubmitConsultationUseCase submitConsultationUseCase;
    private final GetConsultationUseCase getConsultationUseCase;

    public ConsultationController(GetQuestionsUseCase getQuestionsUseCase,
                                 SubmitConsultationUseCase submitConsultationUseCase,
                                 GetConsultationUseCase getConsultationUseCase) {
        this.getQuestionsUseCase = getQuestionsUseCase;
        this.submitConsultationUseCase = submitConsultationUseCase;
        this.getConsultationUseCase = getConsultationUseCase;
    }

    @Operation(
            summary = "Get questions",
            description = "Get consultation questions for a product"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid product ID", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/questions")
    public ResponseEntity<QuestionListResponse> getQuestions(
            @Parameter(description = "Product ID", example = "pear-allergy")
            @RequestParam(name = "productId", defaultValue = "pear-allergy") String productId) {

        List<Question> questions = getQuestionsUseCase.getQuestions(productId);
        QuestionListResponse response = QuestionListResponse.from(productId, questions);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Submit consultation",
            description = "Submit answers and get eligibility result"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ConsultationResponse> submitConsultation(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Consultation answers",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SubmitConsultationRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Eligible Patient",
                                            summary = "Eligible for treatment",
                                            value = """
                                                    {
                                                      "productId": "pear-allergy",
                                                      "answers": [
                                                        {"questionId": "Q1", "answer": "YES"},
                                                        {"questionId": "Q2", "answer": "YES"},
                                                        {"questionId": "Q3", "answer": "NO"}
                                                      ]
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Ineligible Patient",
                                            summary = "Anaphylaxis history",
                                            value = """
                                                    {
                                                      "productId": "pear-allergy",
                                                      "answers": [
                                                        {"questionId": "Q1", "answer": "YES"},
                                                        {"questionId": "Q2", "answer": "YES"},
                                                        {"questionId": "Q3", "answer": "YES"},
                                                        {"questionId": "Q4", "answer": "NO"}
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody SubmitConsultationRequest request) {

        Consultation consultation = submitConsultationUseCase.submitConsultation(
                request.getProductId(),
                request.toAnswers()
        );

        ConsultationResponse response = ConsultationResponse.from(consultation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Get consultation",
            description = "Get consultation by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConsultationResponse> getConsultation(
            @Parameter(description = "Consultation ID")
            @PathVariable String id) {
        return getConsultationUseCase.getConsultation(id)
                .map(consultation -> ResponseEntity.ok(ConsultationResponse.from(consultation)))
                .orElse(ResponseEntity.notFound().build());
    }
}
