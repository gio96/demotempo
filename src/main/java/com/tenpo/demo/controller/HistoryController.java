package com.tenpo.demo.controller;

import com.tenpo.demo.dto.ApiCallHistoryDto;
import com.tenpo.demo.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "History", description = "History calls")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(
            summary = "Retrieve call history",
            description = "Retrieves a paginated list of API call history.")
    @GetMapping
    public ResponseEntity<Page<ApiCallHistoryDto>> getCallHistory(
            @Parameter(description = "The page number of results to fetch (default is 0)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "The number of records per page (default is 10)")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(historyService.getApiCallHistory(page, size));
    }
}
