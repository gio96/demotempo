package com.tenpo.demo;

import com.tenpo.demo.config.CacheConfig;
import com.tenpo.demo.dto.CalculationRequest;
import com.tenpo.demo.dto.CalculationResponse;
import com.tenpo.demo.exception.ExternalServiceException;
import com.tenpo.demo.service.CalculationService;
import com.tenpo.demo.service.ExternalPercentageService;
import com.tenpo.demo.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CalculationServiceTest {

    @Mock
    private ExternalPercentageService percentageService;

    @Mock
    private HistoryService historyService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(CacheConfig.PERCENTAGE_CACHE_NAME)).thenReturn(cache);
        calculationService = new CalculationService(percentageService, historyService, cacheManager);
    }

    @Test
    void testGetCurrentPercentage_success() {
        // Arrange
        double expectedPercentage = 15.0;
        when(percentageService.getPercentage()).thenReturn(expectedPercentage);

        // Act
        Double percentage = calculationService.getCurrentPercentage();

        // Assert
        assertNotNull(percentage);
        assertEquals(expectedPercentage, percentage);
    }

    @Test
    void testGetCurrentPercentage_serviceFailure() {
        // Arrange
        when(percentageService.getPercentage()).thenThrow(ExternalServiceException.class);

        // Act
        Double percentage = calculationService.getCurrentPercentage();

        // Assert
        assertNull(percentage);
        verify(percentageService, times(1)).getPercentage();
    }

    @Test
    void testCalculateWithPercentage_noCache_noError() {
        // Arrange
        CalculationRequest request = new CalculationRequest(10.0, 20.0);
        double expectedPercentage = 10.0;
        double expectedResult = (10 + 20) * (1 + expectedPercentage / 100);
        when(percentageService.getPercentage()).thenReturn(expectedPercentage);

        // Act
        CalculationResponse response = calculationService.calculateWithPercentage(request);

        // Assert
        assertEquals(expectedResult, response.result());
        assertFalse(response.cachedPercentageUsed());
        verify(historyService, times(1)).logApiCall(any(), any(), any(), isNull());
    }

    @Test
    void testCalculateWithPercentage_usingCache() {
        // Arrange
        CalculationRequest request = new CalculationRequest(10.0, 20.0);
        double cachedPercentage = 5.0;
        double expectedResult = (10 + 20) * (1 + cachedPercentage / 100);
        when(percentageService.getPercentage()).thenReturn(null);
        when(cache.get("currentPercentage", Double.class)).thenReturn(cachedPercentage);

        // Act
        CalculationResponse response = calculationService.calculateWithPercentage(request);

        // Assert
        assertEquals(expectedResult, response.result());
        assertTrue(response.cachedPercentageUsed());
        verify(historyService, times(1)).logApiCall(any(), any(), any(), isNull());
    }
}
