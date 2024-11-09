package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

@Slf4j
@Service
public class LineReader {

    public Flux<Graph> read(Flux<FilePart> upload) {
        return upload.flatMap(filePart ->
            DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    String content = dataBuffer.toString(StandardCharsets.UTF_8);
                    DataBufferUtils.release(dataBuffer);
                    return content;
                })
                .flatMapMany(content -> {
                    if (content.trim().isEmpty()) {
                        return Flux.empty();
                    }
                    return Flux.fromArray(content.split("\\R"))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(this::convertToGraph)
                        .onErrorContinue((e, line) -> log.warn("Skipping malformed line: {}", line))
                        .switchIfEmpty(Mono.error(new RuntimeException("Malformed File Input")));
                })
        )
        .log(getClass().getSimpleName(), Level.INFO);
    }

    private Graph convertToGraph(String line) {
        Graph graph = new Graph();

        try {
            String[] points = line.split(",\\s*");
            for (String point : points) {
                if (point.length() < 3) {
                    throw new IllegalArgumentException("Invalid point format: " + point);
                }
                String start = point.substring(0, 1);
                String end = point.substring(1, 2);
                int latency = Integer.parseInt(point.substring(2));

                log.info("Parsed point: {} -> {} with latency {}", start, end, latency);
                graph.addEdge(new Point(start, end, latency));
            }
        } catch (Exception e) {
            log.error("Error parsing line: {} - {}", line, e.getMessage());
            throw new RuntimeException("Malformed File Input");
        }

        return graph;
    }
}