package com.tenpo.demo.controller;

import com.tenpo.demo.dto.CalculationRequest;
import com.tenpo.demo.dto.CalculationResponse;
import com.tenpo.demo.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Calculation", description = "Percentage calculation operations")
@RestController
@RequestMapping("/api/calculate")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculationService calculationService;

    @Operation(
            summary = "Calculate with dynamic percentage",
            description = "Returns the sum of two numbers plus a dynamic percentage",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful calculation"),
                    @ApiResponse(responseCode = "503", description = "External service unavailable")
            })
    @PostMapping
    public ResponseEntity<CalculationResponse> calculate(
            @Parameter(description = "Numbers to calculate", required = true)
            @Valid @RequestBody CalculationRequest request) {
        return ResponseEntity.ok(calculationService.calculateWithPercentage(request));
    }
}
