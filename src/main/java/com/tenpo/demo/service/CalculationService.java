package com.tenpo.demo.service;

import com.tenpo.demo.config.CacheConfig;
import com.tenpo.demo.dto.CalculationRequest;
import com.tenpo.demo.dto.CalculationResponse;
import com.tenpo.demo.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationService {

    public static final String ENDPOINT_PATH = "/api/calculate";
    private final ExternalPercentageService percentageService;
    private final HistoryService historyService;

    @Cacheable(value = CacheConfig.PERCENTAGE_CACHE_NAME, unless = "#result == null")
    @Retryable(
            value = {ExternalServiceException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 100))
    public Double getCurrentPercentage() {
        log.info("Fetching fresh percentage from external service");
        return percentageService.getPercentage();
    }

    public CalculationResponse calculateWithPercentage(CalculationRequest request) {
        double sum = request.num1() + request.num2();
        double percentage;
        String error = null;

        try {
            percentage = getCurrentPercentage();
            log.debug("Using percentage: {}%", percentage);
        } catch (Exception e) {
            error = "External service unavailable and no cached value available";
            log.error(error);
            throw new ExternalServiceException(error);
        }

        double result = sum * (1 + percentage / 100);
        CalculationResponse response = new CalculationResponse(result);

        // Async history logging
        historyService.logApiCall(
                ENDPOINT_PATH,
                request.toString(),
                response.toString(),
                error
        );

        return response;
    }

}
