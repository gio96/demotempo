package com.tenpo.demo.service;

import com.tenpo.demo.config.CacheConfig;
import com.tenpo.demo.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class ExternalPercentageService {

    private final double minPercentage;
    private final double maxPercentage;
    private final double failureRate;

    public ExternalPercentageService(
            @Value("${external.percentage.min:1.0}") double minPercentage,
            @Value("${external.percentage.max:20.0}") double maxPercentage,
            @Value("${external.percentage.failure-rate:0.1}") double failureRate) {
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.failureRate = failureRate;
    }

    /**
     * Obtiene el porcentaje actual del servicio externo (mock)
     *
     * @return valor del porcentaje
     * @throws ExternalServiceException si el servicio falla
     */
    @CachePut(value = CacheConfig.PERCENTAGE_CACHE_NAME, key = "'currentPercentage'")
    public Double getPercentage() throws ExternalServiceException {
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            log.error("External percentage service failed (simulated)");
            throw new ExternalServiceException("External service unavailable");
        }

        double percentage = minPercentage +
                (ThreadLocalRandom.current().nextDouble() *
                        (maxPercentage - minPercentage));
        log.info("New percentage obtained: {}%", percentage);
        return percentage;
    }

    /**
     * Actualización programada cada 30 minutos para mantener fresco el caché
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void scheduledPercentageRefresh() {
        try {
            log.info("Scheduled percentage refresh started");
            getPercentage(); // Esto actualizará el caché automáticamente
        } catch (Exception e) {
            log.warn("Scheduled percentage refresh failed", e);
        }
    }
}