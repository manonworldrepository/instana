package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import reactor.core.publisher.Mono;

@SpringBootTest
class ExactHopsCalculatorTest {

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
    void testCalculatePathsExactHops() {
        ExactHopsCalculator calculator = new ExactHopsCalculator(2);

        Mono<Long> result = calculator.calculatePaths(graph, "A", "C");

        StepVerifier.create(result)
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    void testCalculatePathsNoExactMatch() {
        ExactHopsCalculator exactHopsCalculator = new ExactHopsCalculator(1);

        Mono<Long> result = exactHopsCalculator.calculatePaths(graph, "A", "C");

        StepVerifier.create(result)
            .expectNext(0L)
            .verifyComplete();
    }

    @Test
    void testCalculatePathsExactHopsWithLoop() {
        ExactHopsCalculator exactHopsCalculator = new ExactHopsCalculator(3);

        Mono<Long> result = exactHopsCalculator.calculatePaths(graph, "C", "C");

        StepVerifier.create(result)
            .expectNext(1L)
            .verifyComplete();
    }
}