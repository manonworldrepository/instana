package com.example.instana.service;

import com.example.instana.model.Point;
import com.example.instana.model.Graph;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.logging.Level;

@Service
public class LatencyCalculator {

    public Mono<String> calculate(Graph graph, List<String> nodes) {
        if (nodes == null || nodes.isEmpty() || nodes.size() == 1) {
            return Mono.just("0");
        }

        return Flux.fromIterable(nodes)
            .buffer(2, 1)
            .map(pair -> {
                if (pair.size() < 2) {
                    return 0;
                }

                String start = pair.get(0);
                String end = pair.get(1);

                return graph
                    .getEdges(start)
                    .stream()
                    .filter(point -> point.getEnd().equals(end))
                    .map(Point::getLatency)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("NO SUCH TRACE"));
            })
            .reduce(Integer::sum)
            .map(String::valueOf)
            .onErrorResume(e -> Mono.just(e.getMessage()))
            .log(this.getClass().getSimpleName(), Level.INFO);
    }

}
