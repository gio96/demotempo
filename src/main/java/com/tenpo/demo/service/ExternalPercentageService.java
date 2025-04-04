package com.tenpo.demo.service;

import com.tenpo.demo.config.CacheConfig;
import com.tenpo.demo.exception.ExternalServiceException;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExternalPercentageService {

    private final CacheManager cacheManager;

    // Mock values for demonstration
    private static final double MIN_PERCENTAGE = 1.0;
    private static final double MAX_PERCENTAGE = 20.0;
    private static final double FAILURE_RATE = 0.1; // 10% chance of failure

    public ExternalPercentageService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Obtiene el porcentaje actual del servicio externo (mock)
     * @return valor del porcentaje
     * @throws ExternalServiceException si el servicio falla
     */
    public double getPercentage() throws ExternalServiceException {
        // Simulación de fallo aleatorio
        if (Math.random() < FAILURE_RATE) {
            log.error("External percentage service failed (simulated)");
            throw new ExternalServiceException("External service unavailable (simulated failure)");
        }

        // Simulación de valor aleatorio entre MIN y MAX
        double percentage = MIN_PERCENTAGE + (Math.random() * (MAX_PERCENTAGE - MIN_PERCENTAGE));
        log.info("Retrieved new percentage from external service: {}%", percentage);

        return percentage;
    }

    /**
     * Fuerza la actualización del valor en caché
     */
    public void refreshPercentage() {
        try {
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(CacheConfig.PERCENTAGE_CACHE_NAME);
            if (cache != null) {
                Cache<Object, Object> nativeCache = cache.getNativeCache();
                nativeCache.invalidateAll(); // Limpia todo el caché
                log.info("Percentage cache cleared successfully");

                // Precarga un nuevo valor
                getPercentage();
            }
        } catch (Exception e) {
            log.error("Error refreshing percentage cache", e);
            throw new ExternalServiceException("Cache refresh failed", e);
        }
    }

    /**
     * Versión alternativa que solo actualiza si obtiene un nuevo valor exitosamente
     */
    public void refreshPercentageSafely() {
        try {
            double newPercentage = getPercentage(); // Si falla, lanzará exception
            CaffeineCache cache = (CaffeineCache) cacheManager.getCache(CacheConfig.PERCENTAGE_CACHE_NAME);
            if (cache != null) {
                cache.put("currentPercentage", newPercentage);
                log.info("Percentage cache updated with new value: {}%", newPercentage);
            }
        } catch (Exception e) {
            log.warn("Could not refresh percentage - keeping old value", e);
        }
    }
}
