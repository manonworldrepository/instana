package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.ResultRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class GraphCalculationService {

    @Autowired
    private LatencyCalculator latencyCalculator;

    @Autowired
    private PathsCalculator pathsCalculator;

    @Autowired
    private StringFormatterService stringFormatterService;

    public Flux<String> prepareCalculationsWithIds(Graph graph) {
        List<Mono<?>> calculations = List.of(
            latencyCalculator.calculate(graph, List.of("A", "B", "C")),
            latencyCalculator.calculate(graph, List.of("A", "D")),
            latencyCalculator.calculate(graph, List.of("A", "D", "C")),
            latencyCalculator.calculate(graph, List.of("A", "E", "B", "C", "D")),
            latencyCalculator.calculate(graph, List.of("A", "E", "D")),
            pathsCalculator.setStrategy(new MaxHopsCalculator(3)).calculatePaths(graph, "C", "C"),
            pathsCalculator.setStrategy(new ExactHopsCalculator(4)).calculatePaths(graph, "A", "C"),
            pathsCalculator.setStrategy(new MinLatencyCalculator()).calculatePaths(graph, "A", "C"),
            pathsCalculator.setStrategy(new MinLatencyCalculator()).calculatePaths(graph, "B", "B"),
            pathsCalculator.setStrategy(new MaxLatencyCalculator(30)).calculatePaths(graph, "C", "C")
        );

        List<String> ids = IntStream.rangeClosed(1, 10)
            .mapToObj(Integer::toString).toList();

        return Flux.concat(calculations)
            .zipWithIterable(ids)
            .map(tuple -> new ResultRecord(tuple.getT2(), tuple.getT1()))
            .map(stringFormatterService::formatResultRecord);
    }
}
