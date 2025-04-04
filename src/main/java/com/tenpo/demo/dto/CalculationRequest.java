package com.tenpo.demo.dto;

import jakarta.validation.constraints.NotNull;

public record CalculationRequest(
        @NotNull(message = "num1 cannot be null")
        Double num1,

        @NotNull(message = "num2 cannot be null")
        Double num2
) {}