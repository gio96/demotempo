package com.tenpo.demo.service;

import com.tenpo.demo.config.CacheConfig;
import com.tenpo.demo.dto.CalculationRequest;
import com.tenpo.demo.dto.CalculationResponse;
import com.tenpo.demo.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
    private final CacheManager cacheManager;

    @Cacheable(value = CacheConfig.PERCENTAGE_CACHE_NAME,
            key = "'currentPercentage'",
            unless = "#result == null")
    @Retryable(
            value = {ExternalServiceException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 100))
    public Double getCurrentPercentage() {
        log.info("Fetching fresh percentage from external service");
        try {
            return percentageService.getPercentage();
        } catch (ExternalServiceException e) {
            log.warn("Failed to get percentage from external service");
            return null; // Esto permitirá que se use el valor en caché si existe
        }
    }

    public CalculationResponse calculateWithPercentage(CalculationRequest request) {
        double sum = request.num1() + request.num2();
        Double percentage;
        boolean cachedPercentageUsed = false;
        String error = null;

        percentage = getCurrentPercentage();

        if (percentage == null) {
            // Intentar obtener el valor de la caché directamente
            percentage = getCachedPercentage();
            if (percentage == null) {
                error = "External service unavailable and no cached value available";
                log.error(error);
                throw new ExternalServiceException(error);
            }
            cachedPercentageUsed = true;
            log.warn("Using cached percentage due to external service failure: {}%", percentage);
        } else {
            log.debug("Using fresh percentage: {}%", percentage);
        }

        double result = sum * (1 + percentage / 100);
        CalculationResponse response = new CalculationResponse(result, percentage, cachedPercentageUsed);

        // Async history logging
        historyService.logApiCall(
                ENDPOINT_PATH,
                request.toString(),
                response.toString(),
                error
        );

        return response;
    }

    private Double getCachedPercentage() {
        Cache cache = cacheManager.getCache(CacheConfig.PERCENTAGE_CACHE_NAME);
        if (cache != null) {
            return cache.get("currentPercentage", Double.class);
        }
        return null;
    }
}