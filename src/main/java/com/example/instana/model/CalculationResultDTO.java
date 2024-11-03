package com.example.instana.model;

import java.util.List;
import java.util.stream.Collectors;

public class CalculationResultDTO {
    private final List<ResultRecord> resultRecords;

    public CalculationResultDTO(List<ResultRecord> resultRecords) {
        this.resultRecords = resultRecords;
    }

    public String formatResults() {
        return resultRecords.stream()
                .map(ResultRecord::toString)
                .collect(Collectors.joining("\n"));
    }
}
