package com.example.instana.service;

import com.example.instana.model.Graph;
import reactor.core.publisher.Mono;

public interface PathCalculationStrategy {
    Mono<Long> calculatePaths(Graph graph, String start, String end);
}