package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
class LatencyCalculatorTest {

    private Graph graph;
    private LatencyCalculator latencyCalculator;

    @BeforeEach
    void setUp() {
        graph = new Graph();
        latencyCalculator = new LatencyCalculator();

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
    void testCalculateValidPath() {
        Mono<String> result = latencyCalculator.calculate(graph, List.of("A", "B", "C"));

        StepVerifier.create(result)
            .expectNext("9")
            .verifyComplete();
    }

    @Test
    void testCalculateAnotherValidPath() {
        Mono<String> result = latencyCalculator.calculate(graph, List.of("A", "D", "C"));

        StepVerifier.create(result)
            .expectNext("13")
            .verifyComplete();
    }

    @Test
    void testCalculatePathWithNoSuchTrace() {
        Mono<String> result = latencyCalculator.calculate(graph, List.of("A", "D", "B"));

        StepVerifier.create(result)
            .expectNext("NO SUCH TRACE")
            .verifyComplete();
    }

    @Test
    void testCalculateSingleNodePath() {
        Mono<String> result = latencyCalculator.calculate(graph, List.of("A"));

        StepVerifier.create(result)
            .expectNext("0")
            .verifyComplete();
    }

    @Test
    void testCalculateEmptyPath() {
        Mono<String> result = latencyCalculator.calculate(graph, List.of());

        StepVerifier.create(result)
            .expectNext("0")
            .verifyComplete();
    }
}