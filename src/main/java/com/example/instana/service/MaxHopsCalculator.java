package com.example.instana.service;

import com.example.instana.model.Graph;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

public class MaxHopsCalculator implements PathCalculationStrategy {

    private final int maxHops;

    public MaxHopsCalculator(int maxHops) {
        this.maxHops = maxHops;
    }

    @Override
    public Mono<Long> calculatePaths(Graph graph, String start, String end) {
        return explorePaths(graph, start, end, 0)
            .count()
            .log(getClass().getSimpleName(), Level.INFO);
    }

    private Flux<String> explorePaths(Graph graph, String current, String end, int depth) {
        if (depth > maxHops) {
            return Flux.empty();
        }

        if (current.equals(end) && depth > 0) {
            return Flux.just(current);
        }

        return Flux.fromIterable(graph.getEdges(current))
            .flatMap(point -> explorePaths(graph, point.getEnd(), end, depth + 1))
            .log(getClass().getSimpleName(), Level.INFO);
    }

}
