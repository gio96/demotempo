package com.tenpo.demo.dto;

import java.time.LocalDateTime;

public record ApiCallHistoryDto(
        Long id,
        String endpoint,
        String parameters,
        String response,
        String error,
        LocalDateTime callTime
) {}