package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MinLatencyCalculatorTest {

    private Graph graph;
    private MinLatencyCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new MinLatencyCalculator();
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
    void testCalculateShortestPathSuccess() {
        StepVerifier.create(calculator.calculatePaths(graph, "A", "C"))
            .expectNextMatches(latency -> {
                assertEquals(9, latency, "The shortest path from A to C should have a latency of 9.");
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testCalculateShortestPathWithMultiplePaths() {
        StepVerifier.create(calculator.calculatePaths(graph, "A", "E"))
            .expectNextMatches(latency -> {
                assertEquals(7, latency, "The shortest path from A to E should have a latency of 7.");
                return true;
            })
            .verifyComplete();
    }

    @Test
    void testCalculateShortestPathWithLoop() {
        StepVerifier.create(calculator.calculatePaths(graph, "A", "D"))
            .expectNextMatches(latency -> {
                assertEquals(5, latency, "The shortest path from A to D should have a latency of 5.");
                return true;
            })
            .verifyComplete();
    }
}