package com.tenpo.demo;

import com.tenpo.demo.dto.CalculationRequest;
import com.tenpo.demo.dto.CalculationResponse;
import com.tenpo.demo.exception.ExternalServiceException;
import com.tenpo.demo.service.CalculationService;
import com.tenpo.demo.service.ExternalPercentageService;
import com.tenpo.demo.service.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private ExternalPercentageService percentageService;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        // Reset mocks antes de cada test
        reset(percentageService, historyService);
    }

    @Test
    void calculateWithPercentage_ShouldReturnCorrectResult_WhenServiceWorks() {
        // Arrange
        when(percentageService.getPercentage()).thenReturn(10.0);
        CalculationRequest request = new CalculationRequest(10.0, 20.0);

        // Act
        CalculationResponse response = calculationService.calculateWithPercentage(request);

        // Assert
        assertEquals(33.0, response.result(), 0.001, "El cálculo con 10% debería ser 33");
        verify(historyService).logApiCall(
            eq("/api/calculate"),
            eq(request.toString()),
            eq(response.toString()),
            isNull()
        );
    }

    @Test
    void calculateWithPercentage_ShouldHandleZeroPercentage() {
        // Arrange
        when(percentageService.getPercentage()).thenReturn(0.0);
        CalculationRequest request = new CalculationRequest(10.0, 20.0);

        // Act
        CalculationResponse response = calculationService.calculateWithPercentage(request);

        // Assert
        assertEquals(30.0, response.result(), 0.001, "Con 0% el resultado debería ser igual a la suma");
    }

    @Test
    void calculateWithPercentage_ShouldHandleNegativeNumbers() {
        // Arrange
        when(percentageService.getPercentage()).thenReturn(10.0);
        CalculationRequest request = new CalculationRequest(-10.0, 5.0);

        // Act
        CalculationResponse response = calculationService.calculateWithPercentage(request);

        // Assert
        assertEquals(-5.5, response.result(), 0.001, "Debería manejar números negativos correctamente");
    }

    @Test
    void getCurrentPercentage_ShouldCallExternalService() {
        // Arrange
        when(percentageService.getPercentage()).thenReturn(15.0);

        // Act
        Double result = calculationService.getCurrentPercentage();

        // Assert
        assertEquals(15.0, result, 0.001);
        verify(percentageService).getPercentage();
    }
}