package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MaxLatencyCalculatorTest {

    private Graph graph;

    @BeforeEach
    public void setUp() {
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
    void testCalculatePathsWithStrictLatencyLimit10AC() {
        MaxLatencyCalculator calculator = new MaxLatencyCalculator(10);

        StepVerifier.create(calculator.calculatePaths(graph, "A", "C"))
            .expectNextMatches(paths -> {
                assertEquals(1, paths, "There should be exactly 1 path from A to C with a latency less than 10.");
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testCalculatePathsWithStrictLatencyLimit20AC() {
        MaxLatencyCalculator calculator = new MaxLatencyCalculator(20);

        StepVerifier.create(calculator.calculatePaths(graph, "A", "C"))
                .expectNextMatches(paths -> {
                    assertEquals(5, paths, "There should be exactly 5 path from A to C with a latency less than 20.");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCalculatePathsWithStrictLatencyLimit20BD() {
        MaxLatencyCalculator calculator = new MaxLatencyCalculator(20);

        StepVerifier.create(calculator.calculatePaths(graph, "B", "E"))
                .expectNextMatches(paths -> {
                    assertEquals(3, paths, "There should be exactly 3 path from B to E with a latency less than 20.");
                    return true;
                })
                .verifyComplete();
    }
}
