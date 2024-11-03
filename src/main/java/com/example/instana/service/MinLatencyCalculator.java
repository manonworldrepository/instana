package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

public class MinLatencyCalculator implements PathCalculationStrategy {

    @Override
    public Mono<Long> calculatePaths(Graph graph, String start, String end) {
        Set<String> visited = new HashSet<>();
        int shortestLatency = findShortestPath(graph, start, end, 0, visited, Integer.MAX_VALUE);

        return shortestLatency == Integer.MAX_VALUE
                ? Mono.error(new RuntimeException("NO SUCH TRACE"))
                : Mono.just((long) shortestLatency);
    }

    private int findShortestPath(Graph graph, String current, String target, int currentLatency, Set<String> visited, int shortestLatency) {
        if (current.equals(target) && currentLatency > 0) {
            return Math.min(shortestLatency, currentLatency);
        }

        visited.add(current);

        for (Point point : graph.getEdges(current)) {
            if (!visited.contains(point.getEnd()) || point.getEnd().equals(target)) {
                shortestLatency = findShortestPath(
                    graph,
                    point.getEnd(),
                    target,
                    currentLatency + point.getLatency(),
                    visited, shortestLatency
                );
            }
        }

        visited.remove(current);

        return shortestLatency;
    }
}
