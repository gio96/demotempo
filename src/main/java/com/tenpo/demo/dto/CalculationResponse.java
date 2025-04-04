package com.tenpo.demo.dto;

public record CalculationResponse(
        double result,
        double percentageUsed,
        boolean cachedPercentageUsed
) {
    @Override
    public String toString() {
        return String.format(
                "CalculationResponse{result=%.2f, percentageUsed=%.2f%%, cachedPercentageUsed=%b}",
                result, percentageUsed, cachedPercentageUsed
        );
    }
}