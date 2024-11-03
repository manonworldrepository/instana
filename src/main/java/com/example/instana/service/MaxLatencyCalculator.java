package com.example.instana.service;

import com.example.instana.model.Graph;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.LinkedList;
import java.util.logging.Level;

public class MaxLatencyCalculator implements PathCalculationStrategy {
    private final int maxLatency;

    public MaxLatencyCalculator(int maxLatency) {
        this.maxLatency = maxLatency;
    }

    @Override
    public Mono<Long> calculatePaths(Graph graph, String start, String end) {
        return countPaths(graph, start, end, 0, new LinkedList<>())
            .reduce(0, Integer::sum)
            .map(Long::valueOf)
            .log(getClass().getSimpleName(), Level.INFO);
    }

    private Flux<Integer> countPaths(Graph graph, String current, String end, int currentLatency, LinkedList<String> path) {
        if (currentLatency >= maxLatency) return Flux.just(0);

        path.add(current);

        Flux<Integer> result = Flux.empty();
        if (current.equals(end) && path.size() > 1) {
            result = Flux.just(1);
        }

        return result.concatWith(
            Flux.fromIterable(graph.getEdges(current))
                .flatMap(point -> countPaths(
                    graph,
                    point.getEnd(),
                    end,
                    currentLatency + point.getLatency(),
                    new LinkedList<>(path)
                ))
                .log(getClass().getSimpleName(), Level.INFO)
        );
    }
}
