# Genovian Pear Allergy Consultation Service

A Spring Boot service for managing online medical consultations, built for Genovia telemedicine platform.

## Problem Solved

Enables patients to complete online consultations by:
- Fetching product-specific questionnaires
- Submitting answers for eligibility assessment
- Retrieving consultation results
- Determining treatment eligibility based on product-specific rules

## Project Structure

```
src/main/java/com/genovia/consultation/
├── domain/              # Business logic (framework-agnostic)
│   ├── model/          # Core entities (Consultation, Question, Answer, etc.)
│   ├── port/           # Interfaces (in/out ports)
│   └── service/        # Business rules (eligibility strategies)
├── application/         # Use cases (orchestration)
└── adapter/            # External integrations
    ├── in/web/         # REST controllers
    └── out/persistence/ # Repositories
```

## Key Design Decisions

### 1. Single-Page Consultation (All Questions at Once)
**Why**: Simplicity and better UX
- Frontend receives all questions in one call
- User can see entire form, navigate back/forward
- No session management for multi-step flow
- Simpler validation (all answers submitted together)

**Tradeoff**: All questions loaded upfront vs progressive disclosure in multi-step wizard

### 2. Hexagonal Architecture (Ports & Adapters)
**Why**: Keeps business logic independent from frameworks
- Domain layer has no Spring dependencies
- Easy to swap persistence/web layers
- Testable without infrastructure

**Tradeoff**: More files/indirection vs simpler layered architecture

### 3. Strategy Pattern for Eligibility Rules
**Why**: Each product has different eligibility criteria
- `PearAllergyEligibilityStrategy` implements product-specific rules
- `EligibilityStrategyFactory` selects the right strategy
- New products = new strategy class (no touching existing code)

**Tradeoff**: More classes vs if/else in single service

### 4. In-Memory Repositories
**Why**: Assessment requirement for simplicity
- ConcurrentHashMap for thread-safety
- Pre-loaded with pear allergy questions

**Tradeoff**: Data lost on restart vs production database

### 5. Product-Specific Review Workflows
**Why**: Different products require different approval processes
- **Pear Allergy (OTC)**: Fully automated, no doctor review needed (`ReviewWorkflow.AUTOMATED`)
- **Hair Loss (Prescription)**: Requires async doctor review via Kafka (`ReviewWorkflow.ASYNC_DOCTOR_REVIEW`)
- WorkflowService routes consultations based on product type
- Kafka event publishing only occurs for products requiring doctor review

**Tradeoff**: More complexity vs one-size-fits-all workflow

**Business Value**: Reflects real telepharmacy regulations where OTC products can be auto-approved but prescriptions need clinical oversight

## How to Extend

### Adding a New Product (e.g., Hair Loss Treatment)

**1. Create eligibility strategy:**
```java
@Component
public class HairLossEligibilityStrategy implements EligibilityStrategy {
    @Override
    public String getProductId() { return "hair-loss"; }

    @Override
    public EligibilityResult evaluate(List<Answer> answers) {
        // Implement hair loss eligibility rules
    }
}
```

**2. Add questions to repository:**
```java
repository.addProductQuestions("hair-loss", hairLossQuestions);
```

**3.** Factory auto-discovers the strategy via Spring

## Running the Application

### Build and Test
```bash
./gradlew build
```

### View Code Coverage
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Start the Server
```bash
./gradlew bootRun
# Server starts on http://localhost:8080
```

### API Documentation
Open http://localhost:8080/swagger-ui.html

## API Endpoints

### Get Questions
```bash
GET /api/consultations/questions?productId=pear-allergy
```

### Submit Consultation
```bash
POST /api/consultations
Content-Type: application/json

{
  "productId": "pear-allergy",
  "answers": [
    {"questionId": "Q1", "answer": "YES"},
    {"questionId": "Q2", "answer": "YES"}
  ]
}
```

### Get Consultation Result
```bash
GET /api/consultations/{consultationId}
```

## Testing

Tests across all layers:
- Unit tests for domain logic
- Controller tests with MockMvc
- Repository tests
- Integration tests
- Coverage reports: `./gradlew jacocoTestReport`

## Production Considerations

### Current Limitations & Enhancements

**Synchronous Eligibility (Current)**
- Returns immediate result based on automated rule checking
- Suitable for simple cases with clear disqualifying factors

**Async Doctor Review (Partial Implementation)**
- User journey mentions doctor reviews "later that day" (Step 5)
- Added: `ConsultationStatus` (PENDING_REVIEW, APPROVED, REJECTED)
- Added: `DoctorReview` model for tracking doctor decisions
- All consultations start with status=PENDING_REVIEW
- Response includes current status and doctor review (if completed)
- GET endpoint allows polling for status updates
- Not yet implemented:
  - Event publishing to message queue
  - Separate doctor review service
  - Automatic status updates
  - WebSocket for real-time notifications
- For now: Manual status updates via `withDoctorReview()` method

**Database Persistence**
- Current: In-memory with `ConcurrentHashMap`
- Production: Swap with JPA repository adapter

**Additional Production Needs:**
1. Authentication/authorization
2. Logging and monitoring
3. Rate limiting
4. API versioning
