package com.example.instana.service;

import com.example.instana.model.Graph;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ExactHopsCalculator implements PathCalculationStrategy {
    private final int exactHops;

    public ExactHopsCalculator(int exactHops) {
        this.exactHops = exactHops;
    }

    @Override
    public Mono<Long> calculatePaths(Graph graph, String start, String end) {
        return explorePaths(graph, start, end, 0)
            .count();
    }

    private Flux<String> explorePaths(Graph graph, String current, String end, int depth) {
        if (depth > exactHops) return Flux.empty();

        if (depth == exactHops && current.equals(end)) {
            return Flux.just(current);
        }

        return Flux.fromIterable(graph.getEdges(current))
            .flatMap(point -> explorePaths(graph, point.getEnd(), end, depth + 1));
    }
}