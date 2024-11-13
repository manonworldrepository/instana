package com.example.instana.controller;

import com.example.instana.model.Graph;
import com.example.instana.model.ResultRecord;
import com.example.instana.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private LatencyCalculator latencyCalculator;

    @Autowired
    private PathsCalculator pathsCalculator;

    @Autowired
    private LineReader reader;

    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    @ResponseStatus(OK)
    public Flux<String> index(@RequestPart("file") Flux<FilePart> file) {
        return reader.read(file)
            .flatMap(graph ->
                prepareCalculationsWithIds(graph)
                    .collectList()
                    .map(results -> String.join("\n", results))
                    .map(graphResults -> "\n\nGraph results:\n" + graphResults)
            )
            .concatWith(Flux.just(""));
    }

    private Flux<String> prepareCalculationsWithIds(Graph graph) {
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
            .map(this::formatResultRecord);
    }

    private String formatResultRecord(ResultRecord resultRecord) {
        return resultRecord.getId() + ". " + resultRecord.getResult();
    }
}
