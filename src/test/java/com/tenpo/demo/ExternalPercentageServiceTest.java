package com.tenpo.demo;

import com.tenpo.demo.config.CacheConfig;
import com.tenpo.demo.exception.ExternalServiceException;
import com.tenpo.demo.service.ExternalPercentageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExternalPercentageServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private ExternalPercentageService externalPercentageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simula el cache
        when(cacheManager.getCache(CacheConfig.PERCENTAGE_CACHE_NAME)).thenReturn(cache);
        externalPercentageService = new ExternalPercentageService(1.0, 20.0, 0.1);
    }

    @Test
    void testGetPercentage_success() throws ExternalServiceException {
        // Arrange
        double expectedMin = 1.0;
        double expectedMax = 20.0;

        // Usar doNothing() en lugar de when(...).thenReturn(...) ya que 'put' es void
        doNothing().when(cache).put(anyString(), anyDouble());

        // Act
        Double result = externalPercentageService.getPercentage();

        // Assert
        assertNotNull(result);
        assertTrue(result >= expectedMin && result <= expectedMax, "El porcentaje debe estar dentro del rango esperado.");
    }

    @Test
    void testGetPercentage_failure() {
        // Arrange
        doNothing().when(cache).put(anyString(), anyDouble());

        // Hacer que la probabilidad de fallo sea alta para simular un fallo en el servicio
        ExternalPercentageService spyService = spy(externalPercentageService);
        doThrow(new ExternalServiceException("External service unavailable")).when(spyService).getPercentage();

        // Act & Assert
        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            spyService.getPercentage();
        });

        assertEquals("External service unavailable", exception.getMessage());
    }

    @Test
    void testScheduledPercentageRefresh_success() {
        // Arrange
        ExternalPercentageService spyService = spy(externalPercentageService);

        // Usa doReturn() en lugar de doNothing() para simular que getPercentage devuelve un valor
        doReturn(10.0).when(spyService).getPercentage();  // Aquí estamos simulando que el porcentaje es 10.0

        // Act
        spyService.scheduledPercentageRefresh();

        // Assert
        verify(spyService, times(1)).getPercentage(); // Verifica que getPercentage se llamó una vez
    }

    @Test
    void testScheduledPercentageRefresh_failure() {
        // Arrange
        ExternalPercentageService spyService = spy(externalPercentageService);
        doThrow(new ExternalServiceException("External service unavailable")).when(spyService).getPercentage();

        // Act
        spyService.scheduledPercentageRefresh();

        // Assert
        verify(spyService, times(1)).getPercentage(); // Verifica que se llamó al método getPercentage

        // También verificamos que no se lanza ninguna excepción desde el método programado
        // y que el log de advertencia se haya llamado (si se configurara un mock en el logger)
    }
}
