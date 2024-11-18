package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class MaxHopsCalculatorTest {

    private Graph graph;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        graph.addEdge(new Point("A", "B", 5));
        graph.addEdge(new Point("B", "C", 4));
        graph.addEdge(new Point("C", "D", 8));
        graph.addEdge(new Point("D", "C", 8));
        graph.addEdge(new Point("D", "E", 6));
        graph.addEdge(new Point("A", "D", 5));
        graph.addEdge(new Point("C", "E", 2));
        graph.addEdge(new Point("E", "B", 3));
        graph.addEdge(new Point("A", "E", 7));
    }

    @Test
    void testCalculatePathsExceedingMaxHops() {
        MaxHopsCalculator maxHopsCalculator = new MaxHopsCalculator(1);

        Mono<Long> result = maxHopsCalculator.calculatePaths(graph, "A", "C");

        StepVerifier.create(result)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void testCalculatePathsWithNoConnections() {
        MaxHopsCalculator maxHopsCalculator = new MaxHopsCalculator(3);

        Mono<Long> result = maxHopsCalculator.calculatePaths(graph, "A", "F");

        StepVerifier.create(result)
                .expectNext(0L)
                .verifyComplete();
    }
}