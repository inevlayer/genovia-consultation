package com.genovia.consultation.domain.service.eligibility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EligibilityStrategyFactoryTest {

    private EligibilityStrategyFactory factory;
    private PearAllergyEligibilityStrategy pearAllergyStrategy;
    private HairLossEligibilityStrategy hairLossStrategy;

    @BeforeEach
    void setUp() {
        pearAllergyStrategy = new PearAllergyEligibilityStrategy();
        hairLossStrategy = new HairLossEligibilityStrategy();

        factory = new EligibilityStrategyFactory(
            List.of(pearAllergyStrategy, hairLossStrategy)
        );
    }

    @Test
    void shouldReturnCorrectStrategy_whenProductIdExists() {

        EligibilityStrategy result = factory.getStrategy("pear-allergy");


        assertNotNull(result);
        assertEquals("pear-allergy", result.getProductId());
        assertInstanceOf(PearAllergyEligibilityStrategy.class, result);
    }

    @Test
    void shouldReturnHairLossStrategy_whenRequestingHairLoss() {

        EligibilityStrategy result = factory.getStrategy("hair-loss");


        assertNotNull(result);
        assertEquals("hair-loss", result.getProductId());
        assertInstanceOf(HairLossEligibilityStrategy.class, result);
    }

    @Test
    void shouldThrowException_whenProductIdDoesNotExist() {

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> factory.getStrategy("non-existent-product")
        );

        assertTrue(exception.getMessage().contains("No eligibility strategy found"));
        assertTrue(exception.getMessage().contains("non-existent-product"));
    }

    @Test
    void shouldThrowException_whenProductIdIsNull() {

        assertThrows(
            IllegalArgumentException.class,
            () -> factory.getStrategy(null)
        );
    }

    @Test
    void shouldReturnTrue_whenStrategyExists() {

        boolean result = factory.hasStrategy("pear-allergy");


        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenStrategyDoesNotExist() {

        boolean result = factory.hasStrategy("unknown-product");

        assertFalse(result);
    }

    @Test
    void shouldHandleEmptyStrategyList() {

        EligibilityStrategyFactory emptyFactory = new EligibilityStrategyFactory(List.of());

        assertThrows(
            IllegalArgumentException.class,
            () -> emptyFactory.getStrategy("pear-allergy")
        );
    }

    @Test
    void shouldRegisterMultipleStrategies() {

        boolean hasPearAllergy = factory.hasStrategy("pear-allergy");
        boolean hasHairLoss = factory.hasStrategy("hair-loss");

        assertTrue(hasPearAllergy);
        assertTrue(hasHairLoss);
    }

    @Test
    void shouldNotThrowException_whenCheckingForNonExistentStrategy() {

        boolean result = factory.hasStrategy("does-not-exist");

        assertFalse(result);

    }

    @Test
    void shouldReturnSameStrategyInstance_whenCalledMultipleTimes() {

        EligibilityStrategy first = factory.getStrategy("pear-allergy");
        EligibilityStrategy second = factory.getStrategy("pear-allergy");

        assertSame(first, second, "Factory should return the same strategy instance");
    }

    @Test
    void shouldHandleSingleStrategy() {

        EligibilityStrategyFactory singleFactory = new EligibilityStrategyFactory(
            List.of(pearAllergyStrategy)
        );

        assertNotNull(singleFactory.getStrategy("pear-allergy"));
        assertThrows(
            IllegalArgumentException.class,
            () -> singleFactory.getStrategy("hair-loss")
        );
    }
}
