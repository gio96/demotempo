package com.tenpo.demo.service;

import com.tenpo.demo.dto.ApiCallHistoryDto;
import com.tenpo.demo.model.ApiCallHistory;
import com.tenpo.demo.repository.ApiCallHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final ApiCallHistoryRepository historyRepository;

    @Async
    public void logApiCall(String endpoint, String parameters, String response, String error) {
        ApiCallHistory history = new ApiCallHistory();
        history.setEndpoint(endpoint);
        history.setParameters(parameters);
        history.setResponse(response);
        history.setError(error);
        history.setCallTime(LocalDateTime.now());

        historyRepository.save(history);
    }

    public Page<ApiCallHistoryDto> getApiCallHistory(int page, int size) {
        return historyRepository.findAll(PageRequest.of(page, size))
                .map(this::convertToDto);
    }

    private ApiCallHistoryDto convertToDto(ApiCallHistory entity) {
        return new ApiCallHistoryDto(
                entity.getId(),
                entity.getEndpoint(),
                entity.getParameters(),
                entity.getResponse(),
                entity.getError(),
                entity.getCallTime()
        );
    }
}
