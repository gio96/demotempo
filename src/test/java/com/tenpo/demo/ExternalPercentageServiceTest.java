package com.tenpo.demo;

import com.tenpo.demo.exception.ExternalServiceException;
import com.tenpo.demo.service.ExternalPercentageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ExternalPercentageServiceTest {
    @InjectMocks
    private ExternalPercentageService percentageService;

    @Test
    void getPercentage_ShouldReturnValidPercentage() {
        double percentage = percentageService.getPercentage();

        assertTrue(percentage >= 1.0 && percentage <= 20.0);
    }

    @Test
    void getPercentage_ShouldThrowWhenServiceFails() {
        // This test will pass 90% of the time due to the random failure simulation
        // In a real test, we'd mock the HTTP client
        assertThrows(ExternalServiceException.class, () -> {
            while (true) {
                percentageService.getPercentage();
            }
        });
    }
}