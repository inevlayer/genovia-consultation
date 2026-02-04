package com.genovia.consultation.domain.service.eligibility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EligibilityStrategyFactory {
    private final Map<String, EligibilityStrategy> productIdToStrategyMapping;

    public EligibilityStrategyFactory(List<EligibilityStrategy> availableStrategies) {
        this.productIdToStrategyMapping = new HashMap<>();
        for (EligibilityStrategy strategy : availableStrategies) {
            productIdToStrategyMapping.put(strategy.getProductId(), strategy);
        }
    }

    public EligibilityStrategy getStrategy(String productId) {
        EligibilityStrategy strategy = productIdToStrategyMapping.get(productId);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No eligibility strategy found for product: " + productId
            );
        }
        return strategy;
    }

    public boolean hasStrategy(String productId) {
        return productIdToStrategyMapping.containsKey(productId);
    }
}
