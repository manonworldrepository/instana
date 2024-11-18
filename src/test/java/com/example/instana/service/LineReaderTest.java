package com.example.instana.service;

import com.example.instana.model.Graph;
import com.example.instana.model.Point;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@SpringBootTest
class LineReaderTest {

    @Value("${spring.sample-input}")
    private String sampleInput;

    @Value("${spring.mixed-input}")
    private String mixedInput;

    @Value("${spring.malformed-input}")
    private String malformedInput;

    @Value("${spring.empty-input}")
    private String emptyInput;

    private final LineReader lineReader = new LineReader();

    private FilePart mockFilePartFromResource(String resourcePath) throws IOException {
        InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath);
        assert resource != null;

        byte[] data = resource.readAllBytes();
        resource.close();

        return new FilePart() {
            @Override
            public String filename() {
                return resourcePath;
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return Mono.fromRunnable(() -> {
                    try {
                        Files.write(dest, data);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }

            @Override
            public String name() {
                return "file";
            }

            @Override
            public HttpHeaders headers() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.TEXT_PLAIN);
                return headers;
            }

            @Override
            public Flux<DataBuffer> content() {
                DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(data);
                return Flux.just(dataBuffer);
            }
        };
    }

    private List<Point> getAllEdges(Graph graph) {
        try {
            Field edgesField = Graph.class.getDeclaredField("edges");
            edgesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, List<Point>> edges = (Map<String, List<Point>>) edgesField.get(graph);

            return edges.values().stream()
                .flatMap(List::stream)
                .toList();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access edges in Graph", e);
        }
    }

    private boolean containsEdge(List<Point> edges, String start, String end, int latency) {
        return edges.stream()
            .anyMatch(point -> point.getStart().equals(start) &&
                point.getEnd().equals(end) &&
                point.getLatency() == latency);
    }

    @Test
    void testReadValidInputFromResource() throws IOException {
        FilePart filePart = mockFilePartFromResource(sampleInput);

        StepVerifier.create(lineReader.read(Flux.just(filePart)))
            .expectNextMatches(graph -> {
                List<Point> edges = getAllEdges(graph);
                return edges.size() == 2 &&
                    containsEdge(edges, "A", "B", 5) &&
                    containsEdge(edges, "B", "C", 4);
            })
            .expectNextMatches(graph -> {
                List<Point> edges = getAllEdges(graph);
                return edges.size() == 3 &&
                    containsEdge(edges, "C", "D", 8) &&
                    containsEdge(edges, "D", "C", 8) &&
                    containsEdge(edges, "D", "E", 6);
            })
            .verifyComplete();
    }

    @Test
    void testReadWithMalformedLineFromResource() throws IOException {
        FilePart filePart = mockFilePartFromResource(mixedInput);

        StepVerifier.create(lineReader.read(Flux.just(filePart)))
            .expectNextMatches(graph -> {
                List<Point> edges = getAllEdges(graph);
                return edges.size() == 2 &&
                    containsEdge(edges, "A", "B", 5) &&
                    containsEdge(edges, "B", "C", 4);
            })
            .expectNextMatches(graph -> {
                List<Point> edges = getAllEdges(graph);
                return edges.size() == 2 &&
                    containsEdge(edges, "C", "D", 8) &&
                    containsEdge(edges, "D", "C", 8);
            })
            .verifyComplete();
    }

    @Test
    void testReadAllMalformedLinesFromResource() throws IOException {
        FilePart filePart = mockFilePartFromResource(malformedInput);

        StepVerifier.create(lineReader.read(Flux.just(filePart)))
            .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                throwable.getMessage().equals("Malformed File Input"))
            .verify();
    }

    @Test
    void testReadEmptyInputFromResource() throws IOException {
        FilePart filePart = mockFilePartFromResource(emptyInput);

        StepVerifier.create(lineReader.read(Flux.just(filePart)))
                .verifyComplete();
    }
}