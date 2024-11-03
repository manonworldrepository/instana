package com.example.instana.service;

import com.example.instana.model.Graph;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PathsCalculator {

    private PathCalculationStrategy strategy;

    public PathsCalculator setStrategy(PathCalculationStrategy strategy) {
        this.strategy = strategy;

        return this;
    }

    public Mono<Long> calculatePaths(Graph graph, String start, String end) {
        return strategy.calculatePaths(graph, start, end);
    }
}
